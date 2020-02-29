package com.finance.library.utils;

import com.finance.library.CodeEnum;
import com.finance.library.entity.RefreshReqEntity;
import com.finance.library.config.ServiceConstants;
import com.finance.library.entity.UserInfoEntity;
import com.finance.library.entity.UserRespEntity;
import com.finance.library.listener.HttpListener;
import com.finance.library.listener.RefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

public class RefreshHelper {
    private final static String TAG = "RefreshHelper";


    /**
     * 向服务端请求刷新accessToken
     *
     * @param refreshReq
     * @param httpListener
     */
    public static void refreshAccessToken(RefreshReqEntity refreshReq, final HttpListener httpListener) {
        FormBody formBody = new FormBody.Builder()
                .add("client_id", refreshReq.getClientId())
                .add("client_secret", refreshReq.getClientSecret())
                .add("grant_type", ServiceConstants.GRANT_TYPE)
                .add("refresh_token", refreshReq.getRefreshToken())
                .build();

        HttpUtil.getInstance().post(ServiceConstants.URL_REFRESH_TOKEN, formBody, httpListener);


    }

    public static void onToken(String responseStr, final RefreshListener refreshListener) {
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            final String accessToken = jsonObject.optString("access_token");
            String expiresIn = jsonObject.optString("expires_in");

            if (accessToken.length() > 0) {
                final UserInfoEntity userInfoEntity = new UserInfoEntity();
                userInfoEntity.setAccessToken(accessToken);
                userInfoEntity.setExpiresIn(expiresIn);
                refreshListener.onSuccess(new UserRespEntity.Builder(CodeEnum.SUCCESS.getCode())
                        .setUserInfoEntity(userInfoEntity)
                        .setMessage("刷新成功")
                        .build());

            } else {
                onError(refreshListener);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            onError(refreshListener);
        }
    }

    public static void onError(int code, String msg, RefreshListener refreshListener) {
        refreshListener.onError(new UserRespEntity.Builder(code)
                .setMessage(msg)
                .build());
    }

    public static void onError(RefreshListener refreshListener) {
        onError(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMsg(), refreshListener);

    }


}
