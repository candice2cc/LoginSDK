package com.finance.library.config;

public class ServiceConstants {

    public static final String URL_LOGIN = "https://app-cas.noxgroup.com/login/third";
    public static final String URL_USER_INFO = "https://app-cas.noxgroup.com/api/user_info";
    public static final String URL_REFRESH_TOKEN = "https://app-cas.noxgroup.com/oauth/token";
    public static final String URL_LOGOUT = "https://app-cas.noxgroup.com/api/logout";
    public static final String URL_SEND_TEL_CODE = "https://app-cas.noxgroup.com/api/login/sms";
    public static final String URL_BIND = "https://app-cas.noxgroup.com/api/third/binding";

    // 成功状态码
    public static final int CODE_OK = 0;
    public static final int CODE_FAIL = -1;


    public static final String SCOPE_USER_INFO_FULL = "userinfo_full";
    public static final String SCOPE_USER_INFO = "userinfo";

    public static final String GRANT_TYPE = "refresh_token";


}
