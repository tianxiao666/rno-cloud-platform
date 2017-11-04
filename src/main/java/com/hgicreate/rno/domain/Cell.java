package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "RNO_LTE_CELL")
public class Cell {
	private static final long serialVersionUID = 1L;

	@Id
    private String cellId;
	private String cellName;
	private long enodebId;
    private long eci;
    private long areaId;
    private String manufacturer;
	private long tac;
	private String bandType;
	private long bandWidth;
    private long bandIndicator;
    private long bandAmount;
    private long earfcn;
    private long pci;
    private String coverType;
	private String coverScene;
	private double longitude;
    private double latitude;
    private long azimuth;
    private long eDowntilt;
    private long mDowntilt;
    private long totalDowntilt;
    private long antennaHeight;
    private String remoteCell;
	private String relatedParam;
	private String relatedResouce;
	private long stationSpace;
}
