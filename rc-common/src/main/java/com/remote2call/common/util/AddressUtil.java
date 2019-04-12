package com.remote2call.common.util;

import org.apache.commons.net.telnet.TelnetClient;

import java.net.InetAddress;

public class AddressUtil {
    public static boolean ping(String ip) {
        try {
            return InetAddress.getByName(ip).isReachable(3000);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean telnet(String ip, Integer port) {
        TelnetClient client = null;
        try {
            client = new TelnetClient();
            client.setConnectTimeout(3000);
            if (port != null) {
                client.connect(ip, port);
            } else {
                client.connect(ip);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (client != null) {
                try {
                    client.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
