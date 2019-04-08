package com.remote2call.server.starter;

import com.remote2call.common.DefinedThreadFactory;
import com.remote2call.server.ShutDownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.net.InetSocketAddress;

public abstract class AbstractLauncher implements Launchable, Closeable {

    private static InetSocketAddress localAddress;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workGroup;

    protected Integer bossCores;
    protected Integer workCores;

    protected ChannelInitializer handlerInitializer;

    public void run() throws Exception {
        bossGroup = new NioEventLoopGroup(bossCores, new DefinedThreadFactory("remote2call-boss"));
        workGroup = new NioEventLoopGroup(workCores, new DefinedThreadFactory("remote2call-work"));
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture future = bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .localAddress(this.localAddress)
                .childHandler(handlerInitializer)
                .bind().sync();
        addShutDownHook();
        future.channel().closeFuture().sync();
    }

    public void init(final ChannelInitializer initializer,
                     final InetSocketAddress address,
                     final Integer bossCores,
                     final Integer workCores) {
        this.handlerInitializer = initializer;
        this.bossCores = bossCores;
        this.workCores = workCores;
        this.localAddress = address;
    }

    public void init(final ChannelInitializer initializer,
                     final String host,
                     final Integer port,
                     final Integer bossCores,
                     final Integer workCores) {
        init(initializer, new InetSocketAddress(host, port), bossCores, workCores);
    }

    public void prepare() {
        logger.info("Execute Prepare-Event");
    }

    public void close() {
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
        if (workGroup != null)
            workGroup.shutdownGracefully();
        logger.info("Execute Close-Event");
    }

    private void addShutDownHook() {
        new ShutDownHook(this).addHook();
    }
}
