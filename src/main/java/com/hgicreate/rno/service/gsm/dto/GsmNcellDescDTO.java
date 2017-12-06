package com.hgicreate.rno.service.gsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GsmNcellDescDTO {
    private String areaName;
    private String dataType;
    private String filename;
    private String recordCount;
    private String createdDate;
}
