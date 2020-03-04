package com.finance.library.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.finance.library.CodeEnum;
import com.finance.library.IPlatform;
import com.finance.library.LoginSDK;
import com.finance.library.Provider;
import com.finance.library.config.ServiceConstants;
import com.finance.library.entity.LoginReqEntity;
import com.finance.library.entity.UserRespEntity;
import com.finance.library.listener.HttpListener;
import com.finance.library.listener.LoginListener;
import com.finance.library.utils.LoginHelper;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class QQPlatform implements IPlatform {
    public static final String TAG = "QQPlatform";

    // 需要使用到的字段
    public static final String KEY_APP_ID = "qq_key_app_id";
    public static final String KEY_APP_SECRET = "qq_key_app_secret";

    // 支持的操作类型：登录
    public static final String LOGIN = "qq_login";

    private static final String SCOPE_ALL = "all"; // 期望得到用户信息


    public static final String PACKAGE_NAME = "com.tencent.mobileqq";

    private IUiListener uiListener;


    @Override
    public String[] getSupportedTypes() {
        return new String[]{LOGIN};
    }

    @Override
    public void checkEnvironment(Context context, String type) {
        String appId = LoginSDK.getInstance().getAppValue(KEY_APP_ID);

        // 1. 检测appId是否为空
        if (TextUtils.isEmpty(appId)) {
            throw new IllegalArgumentException("QQ AppId未被初始化，当前为空");
        }

        // 2. 检测是否安装了QQ
        if (!isAppInstalled(context)) {
            throw new IllegalArgumentException("当前设备上未安装QQ");
        }
    }

    @Override
    public boolean isAppInstalled(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        if (pm == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if (PACKAGE_NAME.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doLogin(Activity activity) {
        Tencent tencent = getTencent(activity);
        if (tencent.isSessionValid()) {
            return;
        }
        final LoginListener loginListener = LoginSDK.getInstance().getLoginListener();

        uiListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                handleResponse(response);

            }

            @Override
            public void onError(UiError uiError) {
                LoginHelper.onError(CodeEnum.ERROR_OTHER.getCode(),uiError.errorMessage,loginListener);
            }

            @Override
            public void onCancel() {
                // 登录取消
                LoginHelper.onError(CodeEnum.LOGIN_CANCEL.getCode(),CodeEnum.LOGIN_CANCEL.getMsg(),loginListener);
            }
        };
        tencent.login(activity, SCOPE_ALL, uiListener);

    }

    @Override
    public void handleResponse(Object response) {
        final LoginListener loginListener = LoginSDK.getInstance().getLoginListener();

        if (response != null) {
            JSONObject jsonResponse = (JSONObject) response;
            try {
                String accessToken = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
                String openId = jsonResponse.getString(Constants.PARAM_OPEN_ID);

                Log.d(TAG, "accessToken:" + accessToken);
                Log.d(TAG, "openId:" + openId);
                // 登录成功
                if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(openId)) {
                    // 请求服务端，通过QQ的accessToken和openId换服务端的accessToken和refreshToken、openid
                    JSONObject codeJson = new JSONObject();
                    codeJson.putOpt("accessToken", accessToken);
                    codeJson.putOpt("openId", openId);

                    LoginReqEntity reqEntity = new LoginReqEntity();
                    reqEntity.setClientId(LoginSDK.getInstance().getAppValue(LoginSDK.KEY_CLIENT_ID));
                    reqEntity.setCode(codeJson.toString());
                    reqEntity.setProvider(Provider.QQ);
                    reqEntity.setScope(ServiceConstants.SCOPE_USER_INFO_FULL);
                    LoginHelper.loginAuth(reqEntity, new HttpListener() {
                        @Override
                        public void onFailure(IOException e) {
                            LoginHelper.onError(loginListener);
                        }

                        @Override
                        public void onResponse(String responseStr) {
                            LoginHelper.onLoginAuth(responseStr, loginListener);

                        }
                    });
                } else {
                    LoginHelper.onError(loginListener);
                }


            } catch (Exception e) {
                LoginHelper.onError(loginListener);
            }
        }
    }

    @Override
    public void onResponse(Activity activity, Intent data) {
        if (uiListener != null) {
            Tencent.handleResultData(data, uiListener);
        }
    }

    public static Tencent getTencent(Context context) {
        return Tencent.createInstance(LoginSDK.getInstance().getAppValue(KEY_APP_ID), context.getApplicationContext());
    }
}
