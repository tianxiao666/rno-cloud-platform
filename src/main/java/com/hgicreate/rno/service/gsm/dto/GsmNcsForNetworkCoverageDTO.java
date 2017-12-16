package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

@Data
public class GsmNcsForNetworkCoverageDTO {
    private String areaName;
    private String name;
    private String bsc;
    private Long recordCount;
    private String meaDate;
}
