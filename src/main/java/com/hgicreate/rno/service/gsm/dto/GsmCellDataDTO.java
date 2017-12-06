package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

@Data
public class GsmCellDataDTO {

    private String cellId;
    private String areaName;
    private String cellName;
    private String lac;
    private String ci;
    private String bcch;
    private String bsic;
}
