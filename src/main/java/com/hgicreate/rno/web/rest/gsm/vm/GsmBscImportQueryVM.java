package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

/**
 * @author zeng.dh1
 */

@Data
public class GsmBscImportQueryVM {
    private String begUploadDate;
    private String endUploadDate;
    private String city;
    private String status;
}
