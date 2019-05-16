package com.remote2call.client;

public interface AsyncRcCallback {
    void success(Object result);
    void fail(Exception e);
}
