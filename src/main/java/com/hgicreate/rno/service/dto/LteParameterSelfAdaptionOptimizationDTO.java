package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class LteParameterSelfAdaptionOptimizationDTO {

    private String cellId;
    private String cellName;
    private String radioAccessRate;
    private String erabSetUpSuccessRate;
    private String rrcConnectionSetUpSuccessRate;
    private String radioDropRate;
    private String radioDropCount;
    private String erabDropRate;
    private String switchRequestCount;
    private String switchSuccessCount;

    public LteParameterSelfAdaptionOptimizationDTO(String cellId, String cellName, String radioAccessRate, String erabSetUpSuccessRate,
                                                   String rrcConnectionSetUpSuccessRate, String radioDropRate, String radioDropCount,
                                                   String erabDropRate, String switchRequestCount, String switchSuccessCount) {
        setCellId(cellId);
        setCellName(cellName);
        setRadioAccessRate(radioAccessRate);
        setErabSetUpSuccessRate(erabSetUpSuccessRate);
        setRrcConnectionSetUpSuccessRate(rrcConnectionSetUpSuccessRate);
        setRadioDropRate(radioDropRate);
        setRadioDropCount(radioDropCount);
        setErabDropRate(erabDropRate);
        setSwitchRequestCount(switchRequestCount);
        setSwitchSuccessCount(switchSuccessCount);
    }
}
