package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class LteKpiChartCoverRateDTO {
    private String cellName;
    private Integer sampleNum;
    private Integer xBelowNegative110;
    private Integer xBetweenNegative110And95;
    private Integer xBetweenNegative95And85;
    private Integer xOnNegative85;
}
