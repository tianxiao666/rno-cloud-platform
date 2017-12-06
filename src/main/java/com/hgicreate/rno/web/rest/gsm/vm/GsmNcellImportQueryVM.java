package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmNcellImportQueryVM {
    private String begUploadDate;
    private String endUploadDate;
    private String status;
    private String provinceId;
    private String cityId;
}
