package com.finance.library;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.finance.library.entity.RefreshReqEntity;
import com.finance.library.listener.HttpListener;
import com.finance.library.listener.LoginListener;
import com.finance.library.listener.RefreshListener;
import com.finance.library.utils.RefreshHelper;
import com.finance.library.utils.SDKUtil;
import com.finance.library.weixin.UserResp;
import com.finance.library.weixin.WeixinHandleActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginSDK {
    public static final String TAG = "LoginSDK";

    public static final String KEY_CLIENT_ID = "key_client_id";
    public static final String KEY_CLIENT_SECRET = "key_client_secret";

    private static LoginSDK INSTANCE;

    private LoginListener loginListener;
    private WeixinHandleActivity.OnCreateListener onCreateListener;

    private Map<String, String> appKeyValues;
    private List<Class<? extends IPlatform>> supportPlatforms;
    private WeakReference<IPlatform> wrPlatform;


    public static LoginSDK getInstance() {
        if (INSTANCE == null) {
            synchronized (LoginSDK.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoginSDK();
                }
            }
        }
        return INSTANCE;
    }


    public void init(Map<String, String> keyValue, List<Class<? extends IPlatform>> platforms) {
        // 存储App登录授权字段
        appKeyValues = keyValue;
        // 支持登录的平台
        supportPlatforms = platforms;
    }


    /**
     * @param activity
     * @param type:操作类型
     * @param loginListener
     */
    public void doLogin(final Activity activity, String type, LoginListener loginListener) {
        // 1. 得到目前支持的平台列表
        ArrayList<IPlatform> platforms = new ArrayList<>();
        for (Class<? extends IPlatform> platformClz : supportPlatforms) {
            platforms.add(SDKUtil.createPlatform(platformClz));
        }

        // 2. 根据userType匹配出一个目标平台
        IPlatform curPlatform = null;
        for (IPlatform platform : platforms) {
            for (String s : platform.getSupportedTypes()) {
                if (s.equals(type)) {
                    curPlatform = platform;
                    break;
                }
            }
        }
        wrPlatform = new WeakReference<>(curPlatform);


        // 3.设置监听器
        this.loginListener = loginListener;


        // 4. 检查当前运行环境
        try {
            if (curPlatform == null) {
                throw new UnsupportedOperationException("未找到支持该操作的平台，当前的操作类型为：" + type);
            } else {
                curPlatform.checkEnvironment(activity, type);
            }
        } catch (Throwable throwable) {
            loginListener.onError(
                    new UserResp.Builder(UserResp.Code.CODE_ERROR)
                            .setMessage(throwable.getMessage())
                            .build()
            );
            return;
        }

        // 5. 执行对应平台的登录
        final IPlatform finalCurPlatform = curPlatform;
        onCreateListener = new WeixinHandleActivity.OnCreateListener() {
            @Override
            public void onCreate(WeixinHandleActivity activity) {
                finalCurPlatform.doLogin(activity);

            }
        };
        activity.startActivity(new Intent(activity, WeixinHandleActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }

    /**
     * 刷新accessToken
     * 1.建议每次应用打开时(首次登录时例外)，都调用一次；
     * 2.当app服务端返回token过期，也调用该接口获取新的token;
     * 3.如果刷新失败，则调用doLogin重新登录
     */
    public void refreshAccessToken(String refreshToken, final RefreshListener refreshListener) {
        RefreshReqEntity refreshReq = new RefreshReqEntity();
        refreshReq.setClientId(getAppValue(KEY_CLIENT_ID));
        refreshReq.setClientSecret(getAppValue(KEY_CLIENT_SECRET));
        refreshReq.setGrantType(RefreshReqEntity.GRANT_TYPE);
        refreshReq.setRefreshToken(refreshToken);
        RefreshHelper.refreshAccessToken(refreshReq, new HttpListener() {
            @Override
            public void onFailure(IOException e) {
                RefreshHelper.onError(refreshListener);
            }

            @Override
            public void onResponse(String responseStr) {
                RefreshHelper.onToken(responseStr, refreshListener);


            }
        });

    }


    /**
     * 登录注销
     */
    public void doLoginOut() {


    }

    public void doBindTel() {

    }

    public void destroy() {

    }

    public LoginListener getLoginListener() {
        if (loginListener == null) {
            loginListener = new LoginListener() {
                @Override
                public void onSuccess(UserResp response) {
                    Log.d(TAG, "登录成功");
                }

                @Override
                public void onError(UserResp response) {
                    Log.d(TAG, "登录失败");

                }

                @Override
                public void onCancel(UserResp response) {
                    Log.d(TAG, "登录取消");

                }
            };
        }
        return loginListener;
    }


    public String getAppValue(String key) {
        return appKeyValues.get(key);
    }

    public IPlatform getCurPlatform() {
        return wrPlatform.get();
    }

    public void onActivityCreate(WeixinHandleActivity activity) {
        onCreateListener.onCreate(activity);
    }
}
