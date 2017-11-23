package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class LteCapicityOptimizationDTO {

    private String cellId;
    private String cellName;
    private String onHighStress;
    private String RRC;
    private String upRate;
    private String downRate;
    private String upFlow;
    private String downFlow;
    private String advice;

    private String cellType;
    private String PCI;
    private String longitude;
    private String latitude;
    private String antennaHeight;
    private String azimuth;
    private String openStationRate;
    private String freqBandType;
    private String downTile;
    private String subFrame;
    private String coverType;
    private String mDowntilt;
    private String specialSubFrame;
    private String referenceSignalPower;
    private String eDowntilt;
    private String isVipStation;
    private String TAC;
    private String rrcCount;
    private String TAL;
    private String PDCCH;
    private String PUSCH;
    private String cellCoverRadii;
    private String antennaModel;
    private String startServiceDate;
    private String bandwidth;
    private String isAntennaClosured;
    private String buildType;
    private String referenceSignalAdjustment;
    private String antennaaCoverRangeAdjustment;
    private String cellRechoosePiorityAdjustment;
    private String switchParanoidAndDelayAdjustment;
    private String switchStrategyThresholdAdjustment;
    private String cellRechooseDelay;
    private String interFrequencyFrequencyOffset;
    private String loadBalanceAdjustment;


    public LteCapicityOptimizationDTO(String cellId, String cellName, String onHighStress, String RRC, String upRate,
                                      String downRate, String upFlow, String downFlow, String advice, String cellType,
                                      String PCI, String longitude, String latitude, String antennaHeight, String azimuth,
                                      String openStationRate, String freqBandType, String downTile, String subFrame, String coverType,
                                      String mDowntilt, String specialSubFrame, String referenceSignalPower, String eDowntilt, String isVipStation,
                                      String TAC, String rrcCount, String TAL, String PDCCH, String PUSCH,
                                      String cellCoverRadii, String antennaModel, String startServiceDate, String bandwidth, String isAntennaClosured,
                                      String buildType, String referenceSignalAdjustment, String antennaaCoverRangeAdjustment,
                                      String cellRechoosePiorityAdjustment,String switchParanoidAndDelayAdjustment,
                                      String switchStrategyThresholdAdjustment, String cellRechooseDelay,
                                      String interFrequencyFrequencyOffset, String loadBalanceAdjustment) {
        setCellId(cellId);
        setCellName(cellName);
        setOnHighStress(onHighStress);
        setRRC(RRC);
        setUpRate(upRate);
        setDownRate(downRate);
        setUpFlow(upFlow);
        setDownFlow(downFlow);
        setAdvice(advice);
        setCellType(cellType);
        setPCI(PCI);
        setLongitude(longitude);
        setLatitude(latitude);
        setAntennaHeight(antennaHeight);
        setAzimuth(azimuth);
        setOpenStationRate(openStationRate);
        setFreqBandType(freqBandType);
        setDownTile(downTile);
        setSubFrame(subFrame);
        setCoverType(coverType);
        setMDowntilt(mDowntilt);
        setSpecialSubFrame(specialSubFrame);
        setReferenceSignalPower(referenceSignalPower);
        setEDowntilt(eDowntilt);
        setIsVipStation(isVipStation);
        setTAC(TAC);
        setRrcCount(rrcCount);
        setTAL(TAL);
        setPDCCH(PDCCH);
        setPUSCH(PUSCH);
        setCellCoverRadii(cellCoverRadii);
        setAntennaModel(antennaModel);
        setStartServiceDate(startServiceDate);
        setBandwidth(bandwidth);
        setIsAntennaClosured(isAntennaClosured);
        setBuildType(buildType);
        setReferenceSignalAdjustment(referenceSignalAdjustment);
        setAntennaaCoverRangeAdjustment(antennaaCoverRangeAdjustment);
        setCellRechoosePiorityAdjustment(cellRechoosePiorityAdjustment);
        setSwitchParanoidAndDelayAdjustment(switchParanoidAndDelayAdjustment);
        setSwitchStrategyThresholdAdjustment(switchStrategyThresholdAdjustment);
        setCellRechooseDelay(cellRechooseDelay);
        setInterFrequencyFrequencyOffset(interFrequencyFrequencyOffset);
        setLoadBalanceAdjustment(loadBalanceAdjustment);
    }



}
