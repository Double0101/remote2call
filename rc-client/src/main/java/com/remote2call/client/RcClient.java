package com.remote2call.client;

import com.remote2call.client.service.ServiceDiscovery;

public class RcClient {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RcClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static <T> T create(Class<T> interfaceClass) {
        return null;
    }

    public void stop() {
    }
}
