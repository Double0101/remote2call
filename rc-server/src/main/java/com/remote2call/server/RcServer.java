package com.remote2call.server;

import com.remote2call.server.service.ServiceRegister;
import com.remote2call.server.starter.AbstractLauncher;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RcServer extends AbstractLauncher {
    private static final Logger logger = LoggerFactory.getLogger(RcServer.class);

    private ServiceRegister serviceRegister;

    private Map<String, Object> handlerMap = new HashMap<>();
//    private volatile ThreadPoolExecutor threadPoolExecutor;

    public RcServer(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;
    }

    public RcServer addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            handlerMap.put(interfaceName, serviceBean);
        }
        return this;
    }

    @Override
    public void prepare() {

    }

    @Override
    public void onStart() throws Exception {
        if (serviceRegister != null) {
            serviceRegister.register(
                    localAddress.getHostName() + ":" + localAddress.getPort()
            );
        }
    }
}
