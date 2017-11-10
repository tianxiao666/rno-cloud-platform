package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LteCellDataRecordDTO {
    private String areaName;
    private String beginTime;
    private String endTime;
    private String jobId;
    private String dataNum;
    private String sysTime;
}
