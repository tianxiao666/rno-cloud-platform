package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

@Data
public class GsmMrrImportQueryVM {
    private String status;
    private Long areaId;
    private Date beginDate;
    private Date endDate;
}
