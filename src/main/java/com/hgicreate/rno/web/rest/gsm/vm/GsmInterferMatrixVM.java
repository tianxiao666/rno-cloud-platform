package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmInterferMatrixVM {
    private Long cityId;
    private String interMatrixType;
    private String begDate;
    private String endDate;
}
