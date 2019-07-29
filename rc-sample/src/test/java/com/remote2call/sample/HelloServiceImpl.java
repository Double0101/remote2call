package com.remote2call.sample;

public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "hello!! " + name;
    }
}
