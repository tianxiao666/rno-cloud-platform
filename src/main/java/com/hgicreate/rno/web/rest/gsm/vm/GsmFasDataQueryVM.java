package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

@Data
public class GsmFasDataQueryVM {
    private Long areaId;
    private String bsc;
    private Date beginTestDate;
    private Date endTestDate;
}
