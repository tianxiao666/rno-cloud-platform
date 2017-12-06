package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmCellDataImportVM {
    private String begUploadDate;
    private String endUploadDate;
    private String status;
    private String province;
    private String city;
}
