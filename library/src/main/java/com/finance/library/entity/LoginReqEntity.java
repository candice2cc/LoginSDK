package com.finance.library.entity;

public class LoginReqEntity {
    public static final String PROVIDER_WEIXIN = "wechat";
    public static final String SCOPE_USER_INFO_FULL = "userinfo_full";
    public static final String SCOPE_USER_INFO = "userinfo";

    private String clientId;
    private String provider;
    private String code;
    private String scope;


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "LoginReqEntity{" +
                "clientId='" + clientId + '\'' +
                ", provider='" + provider + '\'' +
                ", code='" + code + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
