package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

/**
 * @author yang.ch1
 */
@Data
public class GsmCoBsicQueryVM {
    private String reselected;
    private int areaId;
    private int cityId;
    private String bcch;
    private String bsic;
}
