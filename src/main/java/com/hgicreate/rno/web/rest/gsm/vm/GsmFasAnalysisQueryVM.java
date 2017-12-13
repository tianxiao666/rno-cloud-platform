package com.hgicreate.rno.web.rest.gsm.vm;


import lombok.Data;

@Data
public class GsmFasAnalysisQueryVM {

    private long cityId;
    private String cell;
    private String fasMeaBegTime;
    private String fasMeaEndTime;
}
