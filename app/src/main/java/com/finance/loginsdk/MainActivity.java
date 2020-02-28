package com.finance.loginsdk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.finance.library.LoginSDK;
import com.finance.library.listener.LoginListener;
import com.finance.library.listener.RefreshListener;
import com.finance.library.weixin.UserResp;
import com.finance.library.weixin.WeiXinPlatform;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private Button weixinLoginBtn, refreshBtn;

    private String refreshToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weixinLoginBtn = findViewById(R.id.weixin_login_btn);
        refreshBtn = findViewById(R.id.refresh_btn);
        weixinLoginBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.weixin_login_btn:
                LoginSDK.getInstance().doLogin(this, WeiXinPlatform.LOGIN, new LoginListener() {
                    @Override
                    public void onSuccess(UserResp response) {
                        Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                        Log.d(TAG, response.getUserInfoEntity().toString());
                        refreshToken = response.getUserInfoEntity().getRefreshToken();
                    }

                    @Override
                    public void onError(UserResp response) {
                        Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());

                    }

                    @Override
                    public void onCancel(UserResp response) {
                        Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());

                    }
                });
                break;
            case R.id.refresh_btn:
                if (refreshToken != null) {
                    LoginSDK.getInstance().refreshAccessToken(refreshToken, new RefreshListener() {
                        @Override
                        public void onSuccess(UserResp response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                            Log.d(TAG, response.getUserInfoEntity().toString());

                        }

                        @Override
                        public void onError(UserResp response) {
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
