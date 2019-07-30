package com.remote2call.sample;

import com.remote2call.server.RcServer;
import com.remote2call.server.service.ServiceRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootstrapWithoutSpring {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapWithoutSpring.class);
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1:2181";
        ServiceRegister register = new ServiceRegister(serverAddress);
        RcServer server = new RcServer(register);
        HelloService helloService = new HelloServiceImpl();
        server.addService(helloService.getClass().getName(), helloService);
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Exception: {}", e);
        }
    }
}
