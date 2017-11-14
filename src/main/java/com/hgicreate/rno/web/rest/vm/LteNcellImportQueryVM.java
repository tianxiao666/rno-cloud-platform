package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

@Data
public class LteNcellImportQueryVM {
    private String begUploadDate;
    private String endUploadDate;
    private String status;
    private String provinceId;
    private String cityId;
}
