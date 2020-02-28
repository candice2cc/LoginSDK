package com.finance.library.utils;

import com.finance.library.IPlatform;

public class SDKUtil {

    public static IPlatform createPlatform(Class<? extends IPlatform> platformClz) {
        try {
            return platformClz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("platform create error");
    }
}
