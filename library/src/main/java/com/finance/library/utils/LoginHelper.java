package com.finance.library.utils;

import com.finance.library.entity.LoginReqEntity;
import com.finance.library.entity.UserInfoEntity;
import com.finance.library.listener.HttpListener;
import com.finance.library.listener.LoginListener;
import com.finance.library.weixin.UserResp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;

public class LoginHelper {
    private final static String TAG = "LoginHelper";


    /**
     * 向服务端请求登录
     *
     * @param loginReq
     * @param httpListener
     */
    public static void loginAuth(LoginReqEntity loginReq, final HttpListener httpListener) {
        FormBody formBody = new FormBody.Builder()
                .add("client_id", loginReq.getClientId())
                .add("provider", loginReq.getProvider())
                .add("code", loginReq.getCode())
                .add("scope", loginReq.getScope())
                .build();

        HttpUtil.getInstance().post(ServiceConstants.URL_LOGIN, formBody, httpListener);


    }

    public static void onLoginAuth(String responseStr, final LoginListener loginListener) {
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            final String accessToken = jsonObject.optString("access_token");
            String refreshToken = jsonObject.optString("refresh_token");
            final String openId = jsonObject.optString("openid");
            String expiresIn = jsonObject.optString("expires_in");

            if (accessToken.length() > 0) {
                final UserInfoEntity userInfoEntity = new UserInfoEntity();
                userInfoEntity.setAccessToken(accessToken);
                userInfoEntity.setRefreshToken(refreshToken);
                userInfoEntity.setOpenId(openId);
                userInfoEntity.setExpiresIn(expiresIn);

                // 请求服务端，通过accessToken和openid请求用户数据
                LoginHelper.queryUserInfo(accessToken, openId, new HttpListener() {
                    @Override
                    public void onFailure(IOException e) {
                        onError(loginListener);
                    }

                    @Override
                    public void onResponse(String responseStr2) {
                        onUserInfo(responseStr2, userInfoEntity, loginListener);
                    }
                });

            } else {
                onError(loginListener);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            onError(loginListener);
        }
    }

    public static void queryUserInfo(String accessToken, String openId, final HttpListener httpListener) {
        FormBody formBody = new FormBody.Builder()
                .add("access_token", accessToken)
                .add("openid", openId)
                .build();

        HttpUtil.getInstance().post(ServiceConstants.URL_USER_INFO, formBody, httpListener);
    }

    public static void onUserInfo(String responseStr, UserInfoEntity userInfoEntity, LoginListener loginListener) {
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            int code = jsonObject.optInt("code", ServiceConstants.CODE_FAIL);
            JSONObject data = jsonObject.optJSONObject("data");
            if (code == ServiceConstants.CODE_OK && data != null) {
                String nickName = data.optString("nickName");
                String unionId = data.optString("unionid");
                String gender = data.optString("gender");
                String avatar = data.optString("avatar");

                userInfoEntity.setNickName(nickName);
                userInfoEntity.setUnionId(unionId);
                userInfoEntity.setGender(gender);
                userInfoEntity.setAvatar(avatar);

                loginListener.onSuccess(new UserResp.Builder(UserResp.Code.CODE_SUCCESS)
                        .setUserInfoEntity(userInfoEntity)
                        .setMessage("登录成功")
                        .build());

            } else {
                onError(loginListener);

            }


        } catch (JSONException e) {
            e.printStackTrace();
            onError(loginListener);

        }
    }

    public static void onError(LoginListener loginListener) {
        // TODO 失败时的错误处理：根据服务端透传？
        loginListener.onError(new UserResp.Builder(UserResp.Code.CODE_ERROR)
                .setMessage("服务器异常，请稍后重试！")
                .build());
    }


}
