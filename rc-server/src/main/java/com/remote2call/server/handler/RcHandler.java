package com.remote2call.server.handler;

import com.remote2call.common.handler.BaseInboundHandler;
import com.remote2call.common.net.RcRequest;
import com.remote2call.common.net.RcResponse;
import com.remote2call.server.RcServer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.sf.cglib.reflect.FastClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RcHandler extends BaseInboundHandler<RcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RcHandler.class);

    private final Map<String, Object> handlerMap;

    public RcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RcRequest msg) throws Exception {
        RcServer.submit(new Runnable() {
            @Override
            public void run() {
                logger.debug("Receive request " + msg.getRequestId());
                RcResponse response = new RcResponse();
                response.setRequestId(msg.getRequestId());
                try {
                    Object result = handle(msg);
                    response.setResult(result);
                } catch (Throwable t) {
                    response.setError(t.toString());
                    logger.error("RcServer handle request error", t);
                }

                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        logger.debug("Send response for request " + msg.getRequestId());
                    }
                });
            }
        });
    }

    private Object handle(RcRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        logger.debug("request " + serviceClass.getName() + " method " + methodName);
        for (int i = 0; i < parameters.length; ++i) {
            logger.debug(parameterTypes[i].getName() + " : " + parameters[i].toString());
        }
        FastClass serviceFastClass = FastClass.create(serviceClass);
        int methodIndex = serviceFastClass.getIndex(methodName, parameterTypes);

        return serviceFastClass.invoke(methodIndex, serviceBean, parameters);
    }
}
