package com.remote2call.client.service;

import com.remote2call.client.handler.RcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectManager.class);

    private volatile static ConnectManager connectManager;

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    private CopyOnWriteArrayList<RcClientHandler> connectedHandlers = new CopyOnWriteArrayList();
    private Map<InetSocketAddress, RcClientHandler> connectedServerNodes = new ConcurrentHashMap();

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private long connectTimeoutMillis = 6000;
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private volatile boolean isRuning = true;

    private ConnectManager() {}

    public static ConnectManager getInstance() {
        if (connectManager == null) {
            synchronized (ConnectManager.class) {
                if (connectManager == null) {
                    connectManager = new ConnectManager();
                }
            }
        }
        return connectManager;
    }

    /*
     * uncomplish
     */
    public void updateConnectServer(List<String> serverAddresses) {
        if (serverAddresses == null || serverAddresses.size() <= 0) {
            logger.error("No available server node. All server nodes are down !!!");
            connectedHandlers.clear();
        } else {
            HashSet<InetSocketAddress> newAllServerNodeSet = new HashSet<InetSocketAddress>();
            serverAddresses.forEach(address -> {
                String[] array = address.split(":");
                if (array.length == 2) {
                    newAllServerNodeSet.add(
                            new InetSocketAddress(
                                    array[0], Integer.parseInt(array[1])
                            )
                    );
                }
            });

        }
    }

    public void reconnnect(final RcClientHandler handler, final SocketAddress remotePeer) {
        if (handler != null) {
            connectedHandlers.remove(handler);
            connectedServerNodes.remove(handler.getRemotePeer());
        }

        connectServerNode((InetSocketAddress) remotePeer);
    }

    private void connectServerNode(final InetSocketAddress remotePeer) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                    }
                });
                ChannelFuture future = bootstrap.connect(remotePeer);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            logger.debug("Successfully connect to remote server. remote peer = " + remotePeer);
                            RcClientHandler handler = future.channel().pipeline().get(RcClientHandler.class);
                            addHandler(handler);
                        }
                    }
                });
            }
        });
    }

    private void addHandler(RcClientHandler handler) {
        connectedHandlers.add(handler);
        InetSocketAddress address = (InetSocketAddress) handler.getChannel().remoteAddress();
        connectedServerNodes.put(address, handler);

    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    public RcClientHandler chooseHandler() {
        int size = connectedHandlers.size();
        while (isRuning && size <= 0) {
            try {
                boolean available = waitingForHandler();
                if (available) {
                    size = connectedHandlers.size();
                }
            } catch (InterruptedException e) {
                logger.error("Waiting for available node is interrupted! ", e);
                throw new RuntimeException("Can't connect any servers!", e);
            }
        }
        int index = (roundRobin.getAndAdd(1) + size) % size;
        return connectedHandlers.get(index);
    }

    public void stop() {
        isRuning = false;
        for (int i = 0; i < connectedHandlers.size(); ++i) {
            RcClientHandler handler = connectedHandlers.get(i);
            handler.close();
        }
        signalAvailableHandler();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }
}
