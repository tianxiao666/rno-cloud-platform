package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataCollectDTO {
    private Integer dataCollectId;
    private String areaName;
    private String uploadTime;
    private String filename;
    private String fileSize;
    private String launchTime;
    private String completeTime;
    private String account;
    private String fileStatus;
}
