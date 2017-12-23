package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmMrrAnalysisQueryVM {
    private String cityId;
    private String inputCell;
    private String mrrMeaBegDate;
    private String mrrMeaEndDate;
    private String mrrDataType;
    private String mrrCHGRType;

    private String tableName;
}
