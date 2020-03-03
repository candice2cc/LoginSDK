package com.finance.library.noxphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.finance.library.IPlatform;

public class NoxPhonePlatform implements IPlatform {
    // 支持的操作类型：登录
    public static final String LOGIN = "noxphone_login";


    @Override
    public String[] getSupportedTypes() {
        return new String[]{LOGIN};
    }

    @Override
    public void checkEnvironment(Context context, String type) {


    }

    @Override
    public boolean isAppInstalled(Context context) {
        return true;
    }

    @Override
    public void doLogin(Activity activity) {


    }

    @Override
    public void handleResponse(Object response) {

    }

    @Override
    public void onResponse(Activity activity, Intent data) {

    }
}
