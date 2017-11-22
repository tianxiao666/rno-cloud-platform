package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

@Data
public class LteKpiChartVM {

    private String provinceId;
    private String cityId;
    private String mrMeaBegDate;
    private String mrMeaEndDate;
    private String mrDataType;
    private String mrDisMode;
    private String inputCell;
}
