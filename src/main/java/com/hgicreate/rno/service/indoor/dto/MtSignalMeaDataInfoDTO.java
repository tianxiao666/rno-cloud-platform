package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MtSignalMeaDataInfoDTO {

    private long id;
    private String cityName;
    private String buildingName;
    private String floorName;
    private String dmTopic;
    private String longitude;
    private String latitude;
    private String planeX;
    private String planeY;
    private String meaDate;
    private String signalType;
    private String signal;
    private String deviceId;
}