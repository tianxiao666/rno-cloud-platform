package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

@Data
public class StructJobReportDTO {

    private String stage;
    private String startTime;
    private String completeTime;
    private String status;
    private String message;

}
