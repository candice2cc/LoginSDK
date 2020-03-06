package com.finance.loginsdk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.finance.library.LoginSDK;
import com.finance.library.entity.IBaseRespEntity;
import com.finance.library.entity.NoxPhoneCodeEntity;
import com.finance.library.entity.UserRespEntity;
import com.finance.library.listener.IBaseListener;
import com.finance.library.listener.LoginListener;
import com.finance.library.listener.RefreshListener;
import com.finance.library.qq.QQPlatform;
import com.finance.library.weixin.WeiXinPlatform;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";
    private Button weixinLoginBtn, qqLoginBtn, refreshBtn, refreshSyncBtn, logoutBtn, codeBtn, telLoginBtn, telBindBtn, userBtn;


    private EditText telInput, codeInput;


    private String refreshToken, openId, accessToken;

    private JSONObject sendCodeResData;

    private LoginListener loginListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        weixinLoginBtn = findViewById(R.id.weixin_login_btn);
        qqLoginBtn = findViewById(R.id.qq_login_btn);
        refreshBtn = findViewById(R.id.refresh_btn);
        refreshSyncBtn = findViewById(R.id.refresh_sync_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        userBtn = findViewById(R.id.user_btn);

        telInput = findViewById(R.id.tel_input);
        codeInput = findViewById(R.id.code_input);
        codeBtn = findViewById(R.id.code_btn);
        telLoginBtn = findViewById(R.id.tel_login_btn);
        telBindBtn = findViewById(R.id.tel_bind_btn);


        weixinLoginBtn.setOnClickListener(this);
        qqLoginBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        refreshSyncBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        userBtn.setOnClickListener(this);

        codeBtn.setOnClickListener(this);
        telBindBtn.setOnClickListener(this);
        telLoginBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        String phone = telInput.getText().toString();
        String code = codeInput.getText().toString();

        switch (id) {
            case R.id.weixin_login_btn:
                LoginSDK.getInstance().doLogin(this, WeiXinPlatform.LOGIN, getLoginListener());
                break;

            case R.id.qq_login_btn:
                LoginSDK.getInstance().doLogin(this, QQPlatform.LOGIN, getLoginListener());
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
            case R.id.refresh_sync_btn:
                if (refreshToken != null) {

                    AsyncTask task = new AsyncTask() {
                        @Override
                        protected UserRespEntity doInBackground(Object[] objects) {
                            // 同步方法调用
                            UserRespEntity respEntity = LoginSDK.getInstance().refreshAccessToken(refreshToken);
                            Log.d(TAG, "refreshAccessToken 调用");

                            return respEntity;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            if (o instanceof UserRespEntity) {
                                UserRespEntity respEntity = (UserRespEntity) o;
                                Log.d(TAG, "code:" + respEntity.getCode() + ",message:" + respEntity.getMessage());
                            }
                        }
                    };
                    task.execute();
                }
                break;
            case R.id.code_btn:
                if (phone.length() > 0) {
                    LoginSDK.getInstance().doSendCode(phone, new IBaseListener() {
                        @Override
                        public void onSuccess(IBaseRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                            Log.d(TAG, response.getData().toString());
                            sendCodeResData = response.getData();
                        }

                        @Override
                        public void onError(IBaseRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());

                        }
                    });

                }
                break;
            case R.id.tel_bind_btn:
                if (phone.length() > 0 && code.length() > 0 && accessToken != null && openId != null && sendCodeResData != null) {
                    NoxPhoneCodeEntity codeEntity = new NoxPhoneCodeEntity(phone, code, sendCodeResData.optString("requestToken"));
                    LoginSDK.getInstance().doBind(accessToken, openId, codeEntity, new IBaseListener() {
                        @Override
                        public void onSuccess(IBaseRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                        }

                        @Override
                        public void onError(IBaseRespEntity response) {
                            Log.d(TAG, "code:" + response.getCode() + ",message:" + response.getMessage());
                        }
                    });

                }
                break;

            case R.id.tel_login_btn:
                if (phone.length() > 0 && code.length() > 0 && sendCodeResData != null) {
                    NoxPhoneCodeEntity codeEntity = new NoxPhoneCodeEntity(phone, code, sendCodeResData.optString("requestToken"));
                    LoginSDK.getInstance().doLogin(codeEntity, getLoginListener());
                }

                break;

            case R.id.user_btn:
                if (accessToken != null && openId != null) {
                    LoginSDK.getInstance().getUserInfo(accessToken, openId, getLoginListener());
                }

                break;
            default:
                break;
        }
    }

    private LoginListener getLoginListener() {
        if (loginListener == null) {
            loginListener = new LoginListener() {
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
            };
        }
        return loginListener;
    }


}
