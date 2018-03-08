package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chao.xj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CbBuildingDTO {

    private Long id;
    private String buildingName;
    private String prov;
    private String city;
    private String status;
    private String buildType;
    private String phone;
    private String totalFloor;
}
