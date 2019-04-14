package com.remote2call.server.initializer;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.LinkedList;

public abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel> {

    protected LinkedList<ChannelInboundHandler> inbounds;
    protected LinkedList<ChannelOutboundHandler> outbounds;

    abstract public void setInbounds(LinkedList<ChannelInboundHandler> inbounds);
    abstract public void setOutbounds(LinkedList<ChannelOutboundHandler> outbounds);

    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        outbounds.stream().forEach(out -> pipeline.addLast(out));
        inbounds.stream().forEach(in -> pipeline.addLast(in));
    }
}
