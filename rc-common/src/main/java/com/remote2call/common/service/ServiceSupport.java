package com.remote2call.common.service;

import com.remote2call.common.zk.ZkInstance;

public abstract class ServiceSupport {

    protected String registryAddress;

    protected ZkInstance zkInstance;

    public ServiceSupport(String address) {
        registryAddress = address;
    }

    public void connectServer() throws Exception {
        zkInstance = ZkInstance.createZkCluster(registryAddress);
    }

}
