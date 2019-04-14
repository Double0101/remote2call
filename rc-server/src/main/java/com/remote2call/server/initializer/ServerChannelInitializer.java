package com.remote2call.server.initializer;

import com.remote2call.server.initializer.AbstractChannelInitializer;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.LinkedList;

public class ServerChannelInitializer extends AbstractChannelInitializer {

    @Override
    public void setInbounds(LinkedList<ChannelInboundHandler> inbounds) {
        inbounds.addFirst(new HttpObjectAggregator(65536));
        inbounds.addFirst(new HttpRequestDecoder());
        this.inbounds = inbounds;
    }

    @Override
    public void setOutbounds(LinkedList<ChannelOutboundHandler> outbounds) {
        outbounds.addFirst(new ChunkedWriteHandler());
        outbounds.addFirst(new HttpContentCompressor());
        outbounds.addFirst(new HttpResponseEncoder());
        this.outbounds = outbounds;
    }
}
