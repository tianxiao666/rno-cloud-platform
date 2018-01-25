package com.hgicreate.rno.web.rest.indoor.vm;

import lombok.Data;

@Data
public class ApEquipmentDataQueryVM {

    Long apId;
    String apIds;
    String floorId;
    String buildingId;
    String equtSsid;
    String status;
    String equtType;
}
