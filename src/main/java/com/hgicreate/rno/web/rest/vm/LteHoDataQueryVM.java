package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

@Data
public class LteHoDataQueryVM {
    private String provinceId;
    private String cityId;
    private String vendor;
    private String beginRecordDate;
    private String endRecordDate;
}
