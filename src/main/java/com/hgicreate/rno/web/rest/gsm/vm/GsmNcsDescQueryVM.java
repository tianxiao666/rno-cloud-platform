package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

@Data
public class GsmNcsDescQueryVM {
    private Long areaId;
    private String factory;
    private String bsc;
    private Date beginTestDate;
    private Date endTestDate;
}
