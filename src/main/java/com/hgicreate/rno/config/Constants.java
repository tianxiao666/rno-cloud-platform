package com.hgicreate.rno.config;

/**
 * 应用程序常量
 */
public final class Constants {

    public static final String ANONYMOUS_USER = "anonymous";
    public static final String ANONYMOUS_NAME = "网优用户";

    public static final String SYSTEM_ACCOUNT = "system";

    public static final Integer[] E_FREQUENCE = {38950, 39000, 39050, 39150, 39250};
    public static final Integer WEAK_COVERAGE_RSRP_THRESHOLD = -110;
    public static final Integer WEAK_COVERAGE_RS_SINR_THRESHOLD = -3;

    public static final String ADMIN_CONSOLE_CLIENT_ID = "security-admin-console";
    public static final String ADMIN_CLI_CLIENT_ID = "admin-cli";

    /**
     * 缺省城市：广州
     */
    public static final Long DEFAULT_AREA = 440106L;

    private Constants() {
    }
}
