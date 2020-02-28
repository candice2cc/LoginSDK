package com.finance.library.utils;

import com.finance.library.entity.IBaseRespEntity;
import com.finance.library.listener.HttpListener;
import com.finance.library.listener.IBaseListener;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static void onLogout(String responseStr, final IBaseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            int code = jsonObject.optInt("code");
            String message = jsonObject.optString("msg");
            if (code == 0) {
                // 注销成功
                listener.onSuccess(new IBaseRespEntity(CodeEnum.SUCCESS.getCode(), "注销成功"));
            } else {
                onError(code, message, listener);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onError(listener);
        }
    }

    public static void onError(int code, String message, IBaseListener listener) {
        // 失败的返回处理：根据服务端透传
        listener.onError(new IBaseRespEntity(code, message));
    }

    public static void onError(IBaseListener listener) {
        onError(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMsg(), listener);

    }
}
