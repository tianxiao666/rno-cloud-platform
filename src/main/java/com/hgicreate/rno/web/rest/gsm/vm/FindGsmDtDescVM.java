package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

import java.util.Date;

@Data
public class FindGsmDtDescVM {
    private Long areaId;
    private String taskName;
    private Date beginDate;
    private Date endDate;
}
