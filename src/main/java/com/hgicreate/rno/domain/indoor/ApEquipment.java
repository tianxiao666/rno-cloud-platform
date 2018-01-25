package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "INDOOR_AP_EQUIPMENT")
public class ApEquipment {

  @Id
  @Column(name = "AP_ID")
  @GeneratedValue(generator="ApEquipmentSeq")
  @SequenceGenerator(name="ApEquipmentSeq",sequenceName="SEQ_INDOOR_AP_EQUIPMENT", allocationSize=1)
  private Long apId;
  private String layerId;
  private String drawMapId;
  private String buildingId;
  private String floorId;
  private String elementId;
  private String svgId;
  private String equtSsid;
  private String macBssid;
  private String equtType;
  private String frequency;
  private String channel;
  private String factory;
  private String brands;
  private String equtModel;
  @Column(name = "POSITION_X")
  private String positionX;
  @Column(name = "POSITION_Y")
  private String positionY;
  private String note;
  private String status;

  @OneToOne
  @JoinColumn(name = "floorId",insertable = false,updatable = false)
  private CbFloor cbFloor;
}
