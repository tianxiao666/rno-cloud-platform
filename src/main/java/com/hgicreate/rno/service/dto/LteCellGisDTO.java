package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LteCellGisDTO {

    private String cellId;
    private String cellName;
    private String manufacturer;
    private String bandType;
    private String bandIndicator;
    private String earfcn;
    private String pci;
    private String coverType;
    private String coverScene;
    private String longitude;
    private String latitude;
    private String azimuth;
    private String eDowntilt;
    private String mDowntilt;
    private String totalDowntilt;
    private String antennaHeight;
    private String remoteCell;
    private String relatedParam;
    private String relatedResouce;
    private String stationSpace;

}
