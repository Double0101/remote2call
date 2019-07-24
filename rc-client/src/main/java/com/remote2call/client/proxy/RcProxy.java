package com.remote2call.client.proxy;

import com.remote2call.client.RcClient;
import com.remote2call.client.RcFuture;
import com.remote2call.client.handler.RcClientHandler;
import com.remote2call.client.service.ConnectManager;
import com.remote2call.common.net.RcRequest;
import com.remote2call.common.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/*
 * service proxy
 */
public class RcProxy<T> implements InvocationHandler, IAsyncProxy {
    private static final Logger logger = LoggerFactory.getLogger(RcProxy.class);

    private static final String METHOD_EQUALS_NAME = "equals";
    private static final String METHOD_HASHCODE_NAME = "hashCode";
    private static final String METHOD_TOSTRING_NAME = "toString";

    private Class<T> clazz;

    public RcProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public RcFuture call(String function, Object... args) {
        RcClientHandler handler = ConnectManager.getInstance().chooseHandler();
        RcRequest request = RequestUtils.createRequest(this.clazz.getName(), function, args);
        return handler.sendRequest(request);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String methodName = method.getName();
            if (METHOD_EQUALS_NAME.equals(methodName)) {
                return proxy == args[0];
            } else if (METHOD_HASHCODE_NAME.equals(methodName)) {
                return System.identityHashCode(proxy);
            } else if (METHOD_TOSTRING_NAME.equals(methodName)) {
                return proxy.getClass().getName()
                        + "@"
                        + Integer.toHexString(System.identityHashCode(proxy))
                        + ", with InvocationHandler"
                        + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        RcRequest request = RequestUtils.createRequest(method, args);
        RcClientHandler handler = ConnectManager.getInstance().chooseHandler();
        RcFuture future = handler.sendRequest(request);
        return future.get();
    }
}
