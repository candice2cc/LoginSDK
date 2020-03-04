package com.finance.library;

/**
 * SDK错误码和文案
 */
public enum CodeEnum {
    SUCCESS(0, "成功"),
    FAIL(-1, "服务器异常，请稍后重试！"),
    LOGIN_CANCEL(-2, "登录取消"),
    DENY(-3, "用户拒绝"),

    ERROR_OTHER(-100, "其他错误"),
    ;


    private final int code;
    private final String msg;

    CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
