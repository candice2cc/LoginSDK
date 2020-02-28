package com.finance.library.weixin;

import com.finance.library.entity.UserInfoEntity;

public class UserResp {
    private int code;
    private String message;
    private UserInfoEntity userInfoEntity;

    public UserResp(Builder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.userInfoEntity = builder.userInfoEntity;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public UserInfoEntity getUserInfoEntity() {
        return userInfoEntity;
    }

    public interface Code {
        int CODE_SUCCESS = 0;
        int CODE_ERROR = -1;
        int CODE_CANCEL = -2;
        int CODE_DENY = -3;
    }

    public static final class Builder {
        // 必填字段
        private int code;

        // 可选字段
        private String message;
        private UserInfoEntity userInfoEntity;

        public Builder(int code) {
            this.code = code;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setUserInfoEntity(UserInfoEntity userInfoEntity) {
            this.userInfoEntity = userInfoEntity;
            return this;
        }

        public UserResp build() {
            return new UserResp(this);
        }
    }

}
