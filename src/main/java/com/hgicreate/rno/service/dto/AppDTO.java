package com.hgicreate.rno.service.dto;

import lombok.Data;

/**
 * @author ke_weixu
 */
@Data
public class AppDTO {
    private Long appId;
    private String appCode;
    private String appName;
    private String appVersion;
    private String appLogo;
    private String appDescription;
}
