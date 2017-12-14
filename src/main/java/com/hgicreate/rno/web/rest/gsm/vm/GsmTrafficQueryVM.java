package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

@Data
public class GsmTrafficQueryVM {

    private String searchType;
    private Long areaId;
    private Date beginTime;
    private Date latestAllowedTime;
    private String stsPeriod;
    private String engName;
    private String cell;
    private String cellChineseName;
    private String stsType;
}
