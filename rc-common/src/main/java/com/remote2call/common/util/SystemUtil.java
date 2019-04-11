package com.remote2call.common.util;

public class SystemUtil {

    public static int systemCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
