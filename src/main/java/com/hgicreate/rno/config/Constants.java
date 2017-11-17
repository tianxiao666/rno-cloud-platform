package com.hgicreate.rno.config;

/**
 * 应用程序常量
 */
public final class Constants {

    //校验可用登录账号的正则表达式
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymous";
    public static final Integer[] E_FREQUENCE = { 38950, 39000, 39050, 39150, 39250 };
    public static final Integer WEAK_COVERAGE_RSRP_THRESHOLD = -110;
    public static final Integer WEAK_COVERAGE_RS_SINR_THRESHOLD = -3;

    private Constants() {
    }
}
