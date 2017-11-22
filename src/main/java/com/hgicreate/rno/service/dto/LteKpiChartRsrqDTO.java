package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class LteKpiChartRsrqDTO {
    private String cellName;
    private Integer sampleNum;
    private Integer xBelow8;
    private Integer xBetween8And15;
    private Integer xOn15;
}
