package com.remote2call.client;

import com.remote2call.client.proxy.IAsyncProxy;
import com.remote2call.client.proxy.RcProxy;
import com.remote2call.client.service.ConnectManager;
import com.remote2call.client.service.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RcClient {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

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

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public static <T> IAsyncProxy createAsync(Class<T> interfaceClass) {
        return new RcProxy<T>(interfaceClass);
    }

    public void stop() throws InterruptedException {
        serviceDiscovery.disconnect();
        ConnectManager.getInstance().stop();
    }
}
