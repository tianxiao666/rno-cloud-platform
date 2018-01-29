package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class MtSignalMeaDataInfo {

  @Id
  private long id;
  @Column(name = "CITY_NAME")
  private String cityName;
  @Column(name = "BUILDING_NAME")
  private String buildingName;
  @Column(name = "FLOOR_NAME")
  private String floorName;
  @Column(name = "DM_TOPIC")
  private String dmTopic;
  @Column(name = "LONGITUDE")
  private String longitude;
  @Column(name = "LATITUDE")
  private String latitude;
  @Column(name = "PLANE_X")
  private String planeX;
  @Column(name = "PLANE_Y")
  private String planeY;
  @Column(name = "MEA_DATE")
  private String meaDate;
  @Column(name = "SIGNAL_TYPE")
  private String signalType;
  @Column(name = "SIGNAL")
  private String signal;
  @Column(name = "DEVICE_ID")
  private String deviceId;
}
