package com.hgicreate.rno.web.rest.indoor.vm;

import lombok.Data;

/**
 * @author chao.xj
 */
@Data
public class CbBuildingDataQueryVM {

    /**
     * query
     */
    String buildingName;
    String buildingType;
    /**
     * edit
     */
    Long buildingId;
    /**
     * status
     */
    String status;
    String buildingIds;
}
