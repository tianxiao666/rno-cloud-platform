package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

import java.util.Date;

@Data
public class NewStationNcellImportQueryVM {
    private String fileCode;
    private String status;
    private Long areaId;
    private Date beginDate;
    private Date endDate;
}
