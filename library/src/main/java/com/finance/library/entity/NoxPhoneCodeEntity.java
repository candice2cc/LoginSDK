package com.finance.library.entity;

import com.finance.library.Provider;

public class NoxPhoneCodeEntity extends CodeEntity {
    private String phone;
    private String phoneCode;
    private String requestToken;

    public NoxPhoneCodeEntity(String phone, String phoneCode, String requestToken) {
        this.setProvider(Provider.NOXPHONE);

        this.phone = phone;
        this.phoneCode = phoneCode;
        this.requestToken = requestToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
