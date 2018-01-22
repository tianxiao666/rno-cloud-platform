package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

@Data
public class GsmNewStationNcellDescQueryVM {
    private Long areaId;
    private String fileCode;
    private Date beginTestDate;
    private Date endTestDate;
}
