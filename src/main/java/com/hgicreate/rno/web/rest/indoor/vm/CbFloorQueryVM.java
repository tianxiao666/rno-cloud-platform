package com.hgicreate.rno.web.rest.indoor.vm;

import lombok.Data;

/**
 * @author chao.xj
 */
@Data
public class CbFloorQueryVM {
    Long floorId;
    Long buildingId;
    String floorName;
    String status;
    /**
     * 批改状态
     */
    String buildingIds;
}
