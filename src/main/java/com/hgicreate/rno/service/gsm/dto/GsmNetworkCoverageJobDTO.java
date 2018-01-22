package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author tao.xj
 */
@Data
public class GsmNetworkCoverageJobDTO {
    private Long id;
    private String status;
    private String cityName;
    private String createdDate;
    private String begMeaTime;
    private String endMeaTime;
    private Integer fileNumber;

}
