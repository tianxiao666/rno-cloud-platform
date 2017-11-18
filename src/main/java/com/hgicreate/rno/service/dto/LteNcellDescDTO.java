package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LteNcellDescDTO {
    private String areaName;
    private String dataType;
    private String filename;
    private String recordCount;
    private String createdDate;
    private String createdUser;
}
