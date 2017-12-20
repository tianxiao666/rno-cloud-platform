package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmStructTaskInfoVM {
    private Long provinceId;
    private Long cityId;
    private String jobName;
    private String taskDescription;
    private String begMeaDate;
    private String endMeaDate;
    private String useEriData;
    private String useHwData;
    private String calConCluster;
    private String calClusterConstrain;
    private String calClusterWeight;
    private String calCellRes;
    private String calIdealDis;
    private String provinceName;
    private String cityName;

    private String sameFreqInterThreshold;
    private String overShootingIdealDisMultiple ;
    private String  betweenNcellIdealDisMultiple ;
    private String  cellCheckTimesIdealDisMultiple;
    private String  cellDetectCiThreshold;
    private String  cellIdealDisReferenceCellNum ;
    private String  gsm900CellFreqNum;
    private String  gsm1800CellFreqNum;
    private String  gsm900CellIdealCapacity;
    private String  gsm1800CellIdealCapacity;
    private String  dlCoverMiniMumSignalStrengthThreshold;
    private String  ulCoverMiniMumSignalStrengthThreshold;
    private String  interFactorMostDistant;
    private String  interFactorSameAndAdjFreqMiniMumThreshold;
    private String  relationNcellCiThreshold;

    private String  totalSampleCntSmall;
    private String  totalSampleCntTooSmall ;
    private String  sameFreqInterCoefBig ;
    private String  sameFreqInterCoefSmall;
    private String  overShootingCoefRfferDistant;
    private String  nonNcellSameFreqInterCoef;
}
