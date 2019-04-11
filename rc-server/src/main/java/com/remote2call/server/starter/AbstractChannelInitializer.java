package com.remote2call.server.starter;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.LinkedList;

public abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel> {

    private LinkedList<ChannelInboundHandler> inbounds;
    private LinkedList<ChannelOutboundHandler> outbounds;

    abstract public void setInbounds(LinkedList inbounds);
    abstract public void setOutbounds(LinkedList outbounds);

    private void init() {
        inbounds = new LinkedList<ChannelInboundHandler>();
        outbounds = new LinkedList<ChannelOutboundHandler>();
        setInbounds(inbounds);
        setOutbounds(outbounds);
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        init();
        final ChannelPipeline pipeline = ch.pipeline();
        outbounds.stream().forEach(out -> pipeline.addLast(out));
        inbounds.stream().forEach(in -> pipeline.addLast(in));
    }
}
