package com.remote2call.client.proxy;

import com.remote2call.client.RcFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RcProxy implements InvocationHandler, IAsyncProxy {



    public RcFuture call(String function, Object... args) {
        return null;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
