package com.remote2call.common.handler;

import com.remote2call.common.net.RcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseInboundHandler extends SimpleChannelInboundHandler<RcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(BaseInboundHandler.class);
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[remote2call] client caught exception", cause);
        ctx.close();
    }
}
