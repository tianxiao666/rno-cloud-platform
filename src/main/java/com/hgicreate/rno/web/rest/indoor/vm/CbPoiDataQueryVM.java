package com.hgicreate.rno.web.rest.indoor.vm;

import lombok.Data;

/**
 * @author chao.xj
 */
@Data
public class CbPoiDataQueryVM {

    Long poiId;
    String poiIds;
    String floorId;
    Long buildingId;
    String poiName;
    String status;
    String poiType;
}
