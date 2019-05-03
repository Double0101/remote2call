package com.remote2call.server.service;

import com.remote2call.common.constant.Constant;
import com.remote2call.common.connect.ServiceSupport;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerRegister extends ServiceSupport {
    private static final Logger logger = LoggerFactory.getLogger(ServerRegister.class);

    public ServerRegister(String address) {
        super(address);
    }

    public void register(String data) throws Exception {
        if (data != null) {
            connectServer();
            if (zkInstance != null) {
                zkInstance.createNode(Constant.ZK_REGISTRY_PATH, new byte[0], CreateMode.PERSISTENT);
                zkInstance.createNode(Constant.ZK_DATA_PATH, data.getBytes(), CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        }
    }
}
