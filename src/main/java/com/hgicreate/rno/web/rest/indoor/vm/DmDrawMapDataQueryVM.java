package com.hgicreate.rno.web.rest.indoor.vm;

import lombok.Data;

@Data
public class DmDrawMapDataQueryVM {
    Long drawMapId;
    String floorId;
    Long buildingId;
    String dmTopic;
    String status;
    String drawMapIds;
}
