package com.remote2call.client.handler;

import com.remote2call.client.RcFuture;
import com.remote2call.common.handler.BaseInboundHandler;
import com.remote2call.common.protocol.RcRequest;
import com.remote2call.common.protocol.RcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class RcClientHandler extends BaseInboundHandler {
    private static final Logger logger = LoggerFactory.getLogger(RcClientHandler.class);

    private ConcurrentHashMap<String, RcFuture> pendingRPC = new ConcurrentHashMap<>();

    private volatile Channel channel;
    private SocketAddress remotePeer;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RcResponse response) throws Exception {
        String requestId = response.getRequestId();
        RcFuture future = pendingRPC.get(requestId);
        if (future != null) {
            pendingRPC.remove(future);
            future.done(response);
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    public RcFuture sendRequest(RcRequest request) {
        final CountDownLatch latch = new CountDownLatch(1);
        RcFuture future = new RcFuture(request);
        pendingRPC.put(request.getRequestId(), future);
        channel.writeAndFlush(request).addListener(f -> {
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        return future;
    }
}
