package com.hgicreate.rno.config;

/**
 * 应用程序常量
 */
public final class Constants {

    //校验可用登录账号的正则表达式
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymous";

    private Constants() {
    }
}
