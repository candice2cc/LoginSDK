package com.finance.loginsdk;

import android.app.Application;
import android.util.Log;

import com.finance.library.IPlatform;
import com.finance.library.LoginSDK;
import com.finance.library.qq.QQPlatform;
import com.finance.library.weixin.WeiXinPlatform;

import java.util.Arrays;

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("pengcong", "AppApplication onCreate");

        LoginSDK.getInstance().init(
                MapBuilder.of(
                        LoginSDK.KEY_CLIENT_ID, AppConstants.CLIENT_ID,
                        LoginSDK.KEY_CLIENT_SECRET, AppConstants.CLIENT_SECRET,

                        WeiXinPlatform.KEY_APP_ID, AppConstants.APP_ID_WX,
                        WeiXinPlatform.KEY_SECRET, AppConstants.APP_SECRET_WX,

                        QQPlatform.KEY_APP_ID, AppConstants.APP_ID_QQ,
                        QQPlatform.KEY_APP_SECRET, AppConstants.APP_SECRET_QQ

                        ),
                Arrays.<Class<? extends IPlatform>>asList(
                        WeiXinPlatform.class, QQPlatform.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LoginSDK.getInstance().destroy();
    }
}
