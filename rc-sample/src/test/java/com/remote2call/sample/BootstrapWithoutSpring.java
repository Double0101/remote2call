package com.remote2call.sample;

import com.remote2call.server.RcServer;
import com.remote2call.server.service.ServiceRegister;

public class BootstrapWithoutSpring {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1:2181";
        ServiceRegister register = new ServiceRegister(serverAddress);
        RcServer server = new RcServer(register);
        HelloService helloService = new HelloServiceImpl();
        server.addService(helloService.getClass().getName(), helloService);
    }
}
