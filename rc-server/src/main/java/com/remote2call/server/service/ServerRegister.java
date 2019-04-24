package com.remote2call.server.service;

import com.remote2call.common.constant.Constant;
import com.remote2call.common.zk.ZkInstance;
import org.apache.zookeeper.CreateMode;
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
                zkInstance.createNode(Constant.ZK_REGISTRY_PATH, new byte[0], CreateMode.PERSISTENT);
                zkInstance.createNode(Constant.ZK_DATA_PATH, data.getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        }
    }

    private ZkInstance zkConnect() throws Exception {
        return ZkInstance.createZkCluster(registerAddress);
    }
}
