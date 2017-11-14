package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LteNcellImportDtDTO {
    private String areaName;
    private String dataType;
    private String fileName;
    private String recordCount;
    private String jobId;
    private String createTime;
}
