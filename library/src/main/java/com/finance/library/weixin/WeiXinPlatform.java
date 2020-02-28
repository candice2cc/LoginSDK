package com.finance.library.weixin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.finance.library.IPlatform;
import com.finance.library.LoginSDK;
import com.finance.library.entity.LoginReqEntity;
import com.finance.library.entity.UserRespEntity;
import com.finance.library.listener.HttpListener;
import com.finance.library.listener.LoginListener;
import com.finance.library.utils.CodeEnum;
import com.finance.library.utils.LoginHelper;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;

public class WeiXinPlatform implements IPlatform {

    public static final String TAG = "WeiXinPlatform";

    // 需要使用到的字段
    public static final String KEY_APP_ID = "weixin_key_app_id";
    public static final String KEY_SECRET = "weixin_key_secret";

    // 支持的操作类型：登录
    public static final String LOGIN = "weixin_login";


    private static final String SCOPE_USER_INFO = "snsapi_userinfo"; // 期望得到用户信息
    private IWXAPI api;
    private IWXAPIEventHandler wxEventHandler;

    @Override
    public String[] getSupportedTypes() {
        return new String[]{LOGIN};
    }

    @Override
    public void checkEnvironment(Context context, String type) {
        String appId = LoginSDK.getInstance().getAppValue(KEY_APP_ID);

        // 1. 检测appId是否为空
        if (TextUtils.isEmpty(appId)) {
            throw new IllegalArgumentException("微信AppId未被初始化，当前为空");
        }

        // 2. 检测是否安装了微信
        if (!isAppInstalled(context)) {
            throw new IllegalArgumentException("当前设备上未安装微信");

        }

    }

    @Override
    public boolean isAppInstalled(Context context) {
        return getApi(context).isWXAppInstalled();
    }

    @Override
    public void doLogin(final Activity activity) {
        SendAuth.Req request = new SendAuth.Req();
        request.scope = SCOPE_USER_INFO;
        getApi(activity).registerApp(LoginSDK.getInstance().getAppValue(KEY_APP_ID));
        wxEventHandler = new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {

            }

            @Override
            public void onResp(BaseResp baseResp) {
                // 登录请求
                Log.d(TAG, "onResp");
                if (baseResp != null && baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                    SendAuth.Resp authResp = ((SendAuth.Resp) baseResp);
                    // 回调，透传authResp
                    handleResponse(authResp);
                }
                activity.finish();
            }
        };

        getApi(activity).sendReq(request);
    }

    @Override
    public void handleResponse(Object response) {
        Log.d(TAG, "handleResponse");

        // 微信登录返回
        if (response instanceof SendAuth.Resp) {
            SendAuth.Resp authResp = (SendAuth.Resp) response;
            final LoginListener loginListener = LoginSDK.getInstance().getLoginListener();

            switch (authResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 登录成功
                    String code = authResp.code;
                    // 请求服务端，通过code换accessToken和refreshToken、openid
                    Log.d(TAG, "code:" + code);
                    LoginReqEntity reqEntity = new LoginReqEntity();
                    reqEntity.setClientId(LoginSDK.getInstance().getAppValue(LoginSDK.KEY_CLIENT_ID));
                    reqEntity.setCode(code);
                    reqEntity.setProvider(LoginReqEntity.PROVIDER_WEIXIN);
                    reqEntity.setScope(LoginReqEntity.SCOPE_USER_INFO_FULL);
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

                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 登录取消
                    loginListener.onCancel(
                            new UserRespEntity.Builder(CodeEnum.LOGIN_CANCEL.getCode())
                                    .setMessage(CodeEnum.LOGIN_CANCEL.getMsg())
                                    .build()
                    );
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 用户拒绝
                    loginListener.onError(
                            new UserRespEntity.Builder(CodeEnum.DENY.getCode())
                                    .setMessage(CodeEnum.DENY.getMsg())
                                    .build()
                    );
                    break;
                default:
                    // 返回
                    loginListener.onError(
                            new UserRespEntity.Builder(CodeEnum.ERROR_OTHER.getCode())
                                    .setMessage(authResp.errStr)
                                    .build()
                    );
                    break;
            }
        }

    }


    private IWXAPI getApi(Context context) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        if (api == null) {
            api = WXAPIFactory.createWXAPI(context.getApplicationContext(), LoginSDK.getInstance().getAppValue(WeiXinPlatform.KEY_APP_ID), true);
        }
        return api;
    }

    public void onResponse(Activity activity, Intent data) {
        Log.d(TAG, "onResponse");

        getApi(activity).handleIntent(data, wxEventHandler);
    }


}
