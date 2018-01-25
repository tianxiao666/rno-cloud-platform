package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "INDOOR_MT_SIGNAL_MEA_DATA")
public class MtSignalMeaData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String signalId;
  private String buildingId;
  private String floorId;
  private String drawMapId;
  private String longitude;
  private String latitude;
  private String signal;
  @Column(name = "PLANE_X")
  private String planeX;
  @Column(name = "PLANE_Y")
  private String planeY;
  private String meaDate;
  private String signalType;
  private String deviceId;
}
