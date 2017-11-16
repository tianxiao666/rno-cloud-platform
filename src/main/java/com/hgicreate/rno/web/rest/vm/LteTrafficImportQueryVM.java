package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

@Data
public class LteTrafficImportQueryVM {
    private String begUploadDate;
    private String endUploadDate;
    private String city;
    private String status;
}
