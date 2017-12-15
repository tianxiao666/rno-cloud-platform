package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

@Data
public class GsmTrafficQualityQueryVM {
    private Integer type;
    private Long areaId;
    private Date beginTime;
    private Date latestAllowedTime;
}
