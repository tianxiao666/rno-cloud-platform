package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

@Data
public class LteKpiQueryVM {
    String cityId;
    String cellNameStr;
    String indexColumnStr;
    String indexColumnNameStr;
    String begTime;
    String endTime;
}
