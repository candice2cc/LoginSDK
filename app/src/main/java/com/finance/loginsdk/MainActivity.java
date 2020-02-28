package com.finance.loginsdk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.finance.library.LoginSDK;
import com.finance.library.entity.IBaseRespEntity;
import com.finance.library.entity.UserRespEntity;
import com.finance.library.listener.IBaseListener;
import com.finance.library.listener.LoginListener;
import com.finance.library.listener.RefreshListener;
import com.finance.library.weixin.WeiXinPlatform;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private Button weixinLoginBtn, refreshBtn, logoutBtn;

    private String refreshToken, openId, accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weixinLoginBtn = findViewById(R.id.weixin_login_btn);
        refreshBtn = findViewById(R.id.refresh_btn);
        logoutBtn = findViewById(R.id.logout_btn);

        weixinLoginBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.weixin_login_btn:
                LoginSDK.getInstance().doLogin(this, WeiXinPlatform.LOGIN, new LoginListener() {
                    @Override
                    public void onSuccess(UserRespEntity response) {
                        Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                        Log.d(TAG, response.getUserInfoEntity().toString());
                        refreshToken = response.getUserInfoEntity().getRefreshToken();
                        accessToken = response.getUserInfoEntity().getAccessToken();
                        openId = response.getUserInfoEntity().getOpenId();
                    }

                    @Override
                    public void onError(UserRespEntity response) {
                        Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());

                    }

                    @Override
                    public void onCancel(UserRespEntity response) {
                        Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());

                    }
                });
                break;
            case R.id.logout_btn:
                if (accessToken != null && openId != null) {
                    LoginSDK.getInstance().doLogout(accessToken, openId, new IBaseListener() {
                        @Override
                        public void onSuccess(IBaseRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                            accessToken = null;
                            refreshToken = null;
                            openId = null;
                        }

                        @Override
                        public void onError(IBaseRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());

                        }
                    });
                }


                break;
            case R.id.refresh_btn:
                if (refreshToken != null) {
                    LoginSDK.getInstance().refreshAccessToken(refreshToken, new RefreshListener() {
                        @Override
                        public void onSuccess(UserRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                            Log.d(TAG, response.getUserInfoEntity().toString());
                            accessToken = response.getUserInfoEntity().getAccessToken();

                        }

                        @Override
                        public void onError(UserRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());

                        }
                    });


                }
                break;
            default:
                break;
        }
    }
}
