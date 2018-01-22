package com.hgicreate.rno.web.rest.gsm.vm;

import lombok.Data;

/**
 * @author tao.xj
 */
@Data
public class GsmCellDataVM {
    private String provinceId;
    private String cityId;
    private String cellId;
    private String cellName;
    private String bsc;
}
