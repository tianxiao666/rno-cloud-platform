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
public class CbPoiDTO {

    private Long poiId;
    private Long buildingId;
    private String floorId;
    private String floorName;
    private String drawMapId;
    private String layerId;
    private String svgId;
    private String elementId;
    private String poiName;
    private String poiType;
    private String poiNote;
    private String prov;
    private String city;
    private String district;
    private String address;
    private String positionX;
    private String positionY;
    private String phone;
    private String picId;
    private String note;
    private String status;
    private String antLac;
    private String antCid;
    private String antFrequency;
    private String antPower;
}
