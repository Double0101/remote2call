package com.remote2call.client.service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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

    private CopyOnWriteArrayList<ChannelInboundHandler> connectedHandlers = new CopyOnWriteArrayList();
    private Map<InetSocketAddress, ChannelInboundHandler> connectedServerNodes = new ConcurrentHashMap();

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

                        }
                    }
                });
            }
        });
    }
}
