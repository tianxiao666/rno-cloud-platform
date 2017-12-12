package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GsmStructAnalysisJobDTO {
    private Long id;
    private String jobName;
    private String status;
    private String cityName;
    private Date startTime;
    private Date completeTime;
    private Date begMeaTime;
    private Date endMeaTime;
    private String resultStorePath;
    private Integer fileNumber;

}
