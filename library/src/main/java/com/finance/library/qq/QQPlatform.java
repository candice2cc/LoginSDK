package com.finance.library.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.finance.library.IPlatform;
import com.finance.library.LoginSDK;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

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

//        // 2. 检测是否安装了QQ
//        if (!isAppInstalled(context)) {
//            throw new IllegalArgumentException("当前设备上未安装微信");
//
//        }
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
        uiListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                if (null == response) {
                    return;
                }
                JSONObject jsonResponse = (JSONObject) response;
                try {
                    String token = jsonResponse.getString(Constants.PARAM_ACCESS_TOKEN);
                    Log.d(TAG, "token:" + token);
                    // TODO


                } catch (Exception e) {
                }
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        };
        tencent.login(activity, SCOPE_ALL, uiListener);

    }

    @Override
    public void handleResponse(Object response) {

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
