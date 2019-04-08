package com.remote2call.server.starter;

import io.netty.channel.ChannelInitializer;

public interface Launchable {
    void run() throws Exception;
    void init(final ChannelInitializer channelInitializer);
    void prepare();
}
