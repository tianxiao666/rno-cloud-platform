package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class LteTrafficDescDTO {

    private String areaName;
    private String beginTime;
    private String endTime;
    private String pmDn;
    private String dataType;
    private String vendor;
    private String recordCount;
    private String createdDate;
    private String jobId;

}
