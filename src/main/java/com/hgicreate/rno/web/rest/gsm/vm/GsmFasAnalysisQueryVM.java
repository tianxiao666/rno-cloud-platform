package com.hgicreate.rno.web.rest.gsm.vm;


import lombok.Data;

/**
 * @author yang.ch1
 */
@Data
public class GsmFasAnalysisQueryVM {

    private long cityId;
    private String cell;
    private String fasMeaBegTime;
    private String fasMeaEndTime;
}
