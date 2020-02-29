package com.finance.library.utils;

import com.finance.library.config.ServiceConstants;
import com.finance.library.listener.HttpListener;

import okhttp3.FormBody;

public class LogoutHelper {
    private final static String TAG = "LogoutHelper";


    /**
     * 登录注销
     *
     * @param accessToken
     * @param openId
     * @param httpListener
     */
    public static void logout(String accessToken, String openId, final HttpListener httpListener) {
        FormBody formBody = new FormBody.Builder()
                .add("access_token", accessToken)
                .add("openid", openId)
                .build();

        HttpUtil.getInstance().post(ServiceConstants.URL_LOGOUT, formBody, httpListener);
    }

}
