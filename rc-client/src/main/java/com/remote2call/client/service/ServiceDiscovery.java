package com.remote2call.client.service;

import com.remote2call.common.connect.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/*
 * connect to zookeeper
 * discover reachable services
 */
public class ServiceDiscovery extends ServiceSupport {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private volatile List<String> dataList = new ArrayList<>();

    public ServiceDiscovery(String address) throws Exception {
        super(address);
        connectServer();
        if (zkInstance != null) {
            zkInstance.watchAll();
        }
    }

    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                logger.debug("[remote2call] using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                logger.debug("[remote2call] using random data: {}", data);
            }
        }
        return data;
    }

}
