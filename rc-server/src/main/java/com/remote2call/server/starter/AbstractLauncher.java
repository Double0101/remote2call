package com.remote2call.server.starter;

import com.remote2call.server.ShutDownHook;
import com.remote2call.server.env.BaseProperty;
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

public class AbstractLauncher implements Launchable, Closeable {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workGroup;

    protected ChannelInitializer handlerInitializer;

    public void run() throws Exception {
        setAvailableProcessors();
        logger.info("BaseProperty.getBossCores [{}] BaseProperty.getWorkCores[{}]", BaseProperty.getBossCores() , BaseProperty.getWorkCores());
        bossGroup = new NioEventLoopGroup(BaseProperty.getBossCores());
        workGroup = new NioEventLoopGroup(BaseProperty.getWorkCores());
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture future = bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.AUTO_READ, true)
                .localAddress(new InetSocketAddress(BaseProperty.getServerHost(), BaseProperty.getServerPort()))
                .childHandler(handlerInitializer)
                .bind().sync();
        addShutDownHook();
        future.channel().closeFuture().sync();
    }

    protected void setAvailableProcessors() {
        String cores = System.getProperty("io.netty.availableProcessors");
        if (cores == null || Integer.parseInt(cores.trim()) <= 0) {
            logger.info("SET SYSTEM PROPERTY [io.netty.availableProcessors]");
            System.setProperty("io.netty.availableProcessors", String.valueOf(BaseProperty.getBossCores()));
        }
    }

    public void init(final ChannelInitializer initializer) {
        this.handlerInitializer = initializer;
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
