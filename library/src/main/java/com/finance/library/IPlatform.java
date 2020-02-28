package com.finance.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public interface IPlatform {



    /**
     * @return 该平台支持的操作类型
     */
    String[] getSupportedTypes();

    void checkEnvironment(Context context, String type);


    boolean isAppInstalled(Context context);

    void doLogin(Activity activity);

    void handleResponse(Object response);

    void onResponse(Activity activity, Intent data);


}
