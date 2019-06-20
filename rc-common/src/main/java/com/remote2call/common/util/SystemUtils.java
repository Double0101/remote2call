package com.remote2call.common.util;

public class SystemUtils {

    public static int systemCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
