package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

@Data
public class GsmInterferMatrixJobDTO {
    private Long id;
    private String status;
    private String cityName;
    private String createdDate;
    private String begMeaTime;
    private String endMeaTime;
    private String dataType;
    private Long recordCount;
}
