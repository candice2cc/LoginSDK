package com.finance.library.entity;

public class UserRespEntity {
    private int code;
    private String message;
    private UserInfoEntity userInfoEntity;

    public UserRespEntity(Builder builder) {
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

        public UserRespEntity build() {
            return new UserRespEntity(this);
        }
    }

}
