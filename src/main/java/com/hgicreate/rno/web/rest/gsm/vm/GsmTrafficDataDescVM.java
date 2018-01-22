package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

/**
 * @author yang.ch1
 */
@Data
public class GsmTrafficDataDescVM {
    private String areaId;
    private String searchType;
    private String beginTime;
    private String latestAllowedTime;
    private String stsPeriod;
    private String bsc;
}
