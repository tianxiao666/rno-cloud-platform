package com.hgicreate.rno.domain.indoor;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "INDOOR_CB_POI")
public class CbPoi {

    @Id
    @GeneratedValue(generator="CbPoiSeq")
    @SequenceGenerator(name="CbPoiSeq",sequenceName="SEQ_INDOOR_CB_POI", allocationSize=5)
    private Long poiId;
    private Long buildingId;
    private String floorId;
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
    @Column(name = "POSITION_X")
    private String positionX;
    @Column(name = "POSITION_Y")
    private String positionY;
    private String phone;
    private String picId;
    private String note;
    private String status;
    private String antLac;
    private String antCid;
    private String antFrequency;
    private String antPower;

    @OneToOne
    @JoinColumn(name = "floorId",insertable = false,updatable = false)
    private CbFloor cbFloor;
}
