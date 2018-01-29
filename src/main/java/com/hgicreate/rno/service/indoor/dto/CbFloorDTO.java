package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CbFloorDTO {

    private Long floorId;
    private Long buildingId;
    private String floorName;
    private String physicalFloor;
    private String floorType;
    private String floorNote;
    private String status;
}
