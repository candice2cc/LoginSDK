package com.finance.library.utils;

import com.finance.library.CodeEnum;
import com.finance.library.config.ServiceConstants;
import com.finance.library.entity.IBaseRespEntity;
import com.finance.library.listener.IBaseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class IBaseHelper {
    public static void onResponse(String responseStr, final IBaseListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            int code = jsonObject.optInt("code", ServiceConstants.CODE_FAIL);
            String message = jsonObject.optString("msg");
            if (code == ServiceConstants.CODE_OK) {
                // 注销成功
                listener.onSuccess(new IBaseRespEntity(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg()));
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
