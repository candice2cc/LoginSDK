package com.finance.library.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.finance.library.LoginSDK;

public class WeixinHandleActivity extends Activity {
    private static final String TAG = "WeixinHandleActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        // 为了防止这个activity关不掉，这里给用户一个点击关闭的功能
        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (savedInstanceState != null) {
            Log.d(TAG,"handleResp");
            handleResp(getIntent());
        } else {
            LoginSDK.getInstance().onActivityCreate(this);
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleResp(intent);
    }

    private void handleResp(Intent data) {
        if (LoginSDK.getInstance().getCurPlatform() != null) {
            LoginSDK.getInstance().getCurPlatform().onResponse(this, data);
        } else {
            Log.e(TAG, "LoginSDK.curPlatform is null");
        }
    }


    public interface OnCreateListener {

        void onCreate(WeixinHandleActivity activity);
    }

}
