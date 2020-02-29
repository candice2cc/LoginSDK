package com.finance.library.utils;

import com.finance.library.CodeEnum;
import com.finance.library.config.ServiceConstants;
import com.finance.library.entity.BindAccountReqEntity;
import com.finance.library.entity.IBaseRespEntity;
import com.finance.library.listener.HttpListener;
import com.finance.library.listener.IBaseListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

public class BindHelper {
    public static void sendTelCode(String clientId, String phone, HttpListener httpListener) {

        FormBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("phone", phone)
                .build();

        HttpUtil.getInstance().post(ServiceConstants.URL_SEND_TEL_CODE, formBody, httpListener);

    }

    public static void onTelCode(String responseStr, IBaseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            int code = jsonObject.optInt("code", ServiceConstants.CODE_FAIL);
            String message = jsonObject.optString("msg");
            JSONObject data = jsonObject.optJSONObject("data");
            if (code == ServiceConstants.CODE_OK && data != null) {
                IBaseRespEntity respEntity = new IBaseRespEntity(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg());
                respEntity.setData(data);
                listener.onSuccess(respEntity);
            } else {
                IBaseHelper.onError(code, message, listener);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            IBaseHelper.onError(listener);

        }
    }


    public static void bindAccount(BindAccountReqEntity bindReq, HttpListener httpListener) {
        FormBody formBody = new FormBody.Builder()
                .add("access_token", bindReq.getAccessToken())
                .add("openid", bindReq.getOpenId())
                .add("client_id", bindReq.getClientId())
                .add("client_secret", bindReq.getClientSecret())
                .add("code", bindReq.getCode())
                .add("provider", bindReq.getProvider())
                .build();

        HttpUtil.getInstance().post(ServiceConstants.URL_BIND, formBody, httpListener);
    }

}
