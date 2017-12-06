package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class ParamCheckQueryVM {
    private String bscStr;
    private String date1;
    private String checkType;
    private Integer cityId;
    private Boolean checkMaxChgr;
    private Boolean checkBaNum;
    private Integer maxNum;
    private Integer minNum;
    private Boolean checkCoBsic;
    private Integer distance;
    private Boolean checkNcellNum;
    private Integer ncellMaxNum;
    private Integer ncellMinNum;
}
