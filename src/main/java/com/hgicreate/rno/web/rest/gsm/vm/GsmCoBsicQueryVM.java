package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

@Data
public class GsmCoBsicQueryVM {
    private String reselected;
    private Integer areaIds;
    private String bcch;
    private String bsic;
}
