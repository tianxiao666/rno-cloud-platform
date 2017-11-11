package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RNO_LTE_CELL")
public class Cell {
	private static final long serialVersionUID = 1L;

	@Id
    private String cellId;
	private String cellName;
	private String enodebId;
    private String eci;
    private String manufacturer;
	private String tac;
	private String bandType;
	private String bandWidth;
    private String bandIndicator;
    private String bandAmount;
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

	@OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
	private Area area;
}
