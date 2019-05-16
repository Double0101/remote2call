package com.remote2call.client.proxy;

import com.remote2call.client.RcFuture;

public interface IAsyncProxy {
    public RcFuture call(String function, Object... args);
}
