package com.remote2call.client.service;

import com.remote2call.common.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class ServiceDiscovery extends ServiceSupport {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    public ServiceDiscovery(String address) throws Exception {
        super(address);
        connectServer();
        if (zkInstance != null) {
            zkInstance.watchNode();
        }
    }
}
