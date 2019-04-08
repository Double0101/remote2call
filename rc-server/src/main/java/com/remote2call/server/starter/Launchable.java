package com.remote2call.server.starter;

import io.netty.channel.ChannelInitializer;

import java.net.InetSocketAddress;

public interface Launchable {
    void run() throws Exception;
    void init(final ChannelInitializer channelInitializer,
              final InetSocketAddress socketAddress,
              final Integer bossCores,
              final Integer workCores);
    void init(final ChannelInitializer channelInitializer,
              final String host,
              final Integer port,
              final Integer bossCores,
              final Integer workCores);
    void prepare();
}
