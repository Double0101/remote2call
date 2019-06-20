package com.remote2call.common.util;

import com.remote2call.common.net.RcRequest;

import java.lang.reflect.Method;
import java.util.UUID;

public class RequestUtils {

    public static RcRequest createRequest(Method method, Object[] args) {
        RcRequest request = newRequestInstance(
                method.getDeclaringClass().getName(),
                method.getName(),
                args);
        request.setParameterTypes(method.getParameterTypes());
        return request;
    }

    public static RcRequest createRequest(String className, String methodName, Object[] args) {
        RcRequest request = newRequestInstance(className, methodName, args);

        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        return request;
    }

    private static RcRequest newRequestInstance(String className, String methodName, Object[] args) {
        RcRequest request = new RcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);
        return request;
    }

    private static Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }
}
