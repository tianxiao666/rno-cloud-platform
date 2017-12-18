package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmParamQueryVM {
    private String cellParam;
    private String cellBsc;
    private String cellDate;
    private String cellForCell;
    private String cellForNcell;
    private String cityId;
    private String type;

    private String[] dateList;
    private String[] bscList;
    private String[] cellList;
 }
