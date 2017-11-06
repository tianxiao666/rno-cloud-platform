package com.hgicreate.rno.service.dto;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.Cell;
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

    public LteCellGisDTO(Cell cell) {
        this(cell.getCellId(), cell.getCellName(), cell.getManufacturer(), cell.getBandType(),
                cell.getBandIndicator(), cell.getEarfcn(), cell.getPci(), cell.getCoverType(),
                cell.getCoverScene(), cell.getLongitude(), cell.getLatitude(), cell.getAzimuth(),
                cell.getEDowntilt(), cell.getMDowntilt(), cell.getTotalDowntilt(), cell.getAntennaHeight(),
                cell.getRemoteCell(), cell.getRelatedParam(), cell.getRelatedResouce(), cell.getStationSpace());
    }
}
