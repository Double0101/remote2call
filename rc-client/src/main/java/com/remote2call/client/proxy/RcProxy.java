package com.remote2call.client.proxy;

import com.remote2call.client.RcFuture;
import com.remote2call.client.handler.RcClientHandler;
import com.remote2call.client.service.ConnectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RcProxy<T> implements InvocationHandler, IAsyncProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(RcProxy.class);

    private Class<T> clazz;

    public RcProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    public RcFuture call(String function, Object... args) {
//        RcClientHandler handler = ConnectManager.getInstance().
        return null;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
