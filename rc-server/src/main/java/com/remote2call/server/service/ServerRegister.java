package com.remote2call.server.service;

import com.remote2call.server.zk.ZkInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerRegister {
    private static final Logger logger = LoggerFactory.getLogger(ServerRegister.class);

    private String registerAddress;

    public ServerRegister(String address) {
        this.registerAddress = address;
    }

    public void register(String data) throws Exception {
        if (data != null) {
            ZkInstance zkInstance = zkConnect();
            if (zkInstance != null) {
                zkInstance.addRootNode();
                zkInstance.createNode(data);
            }
        }
    }

    private ZkInstance zkConnect() throws Exception {
        return ZkInstance.createZkCluster(registerAddress);
    }
}
