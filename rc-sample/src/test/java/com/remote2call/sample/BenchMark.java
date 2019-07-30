package com.remote2call.sample;

import com.remote2call.client.RcClient;
import com.remote2call.client.service.ServiceDiscovery;

public class BenchMark {
    public static void main(String[] args) throws Exception {
        ServiceDiscovery discovery = new ServiceDiscovery("127.0.0.1:2181");
        RcClient client = new RcClient(discovery);
        final HelloService helloService = RcClient.create(HelloService.class);
        String result = helloService.hello("Double0101");
        System.out.println(result);
        client.stop();
    }
}
