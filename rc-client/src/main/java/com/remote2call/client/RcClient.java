package com.remote2call.client;

import com.remote2call.client.proxy.IAsyncProxy;
import com.remote2call.client.proxy.RcProxy;
import com.remote2call.client.service.ConnectManager;
import com.remote2call.client.service.ServiceDiscovery;

import java.lang.reflect.Proxy;

public class RcClient {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RcClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RcClient(ServiceDiscovery discovery) {
        this.serviceDiscovery = discovery;
    }

    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RcProxy<T>(interfaceClass)
        );
    }

    public static <T> IAsyncProxy createAsync(Class<T> interfaceClass) {
        return new RcProxy<T>(interfaceClass);
    }

    public void stop() throws InterruptedException {
        serviceDiscovery.disconnect();
        ConnectManager.getInstance().stop();
    }
}
