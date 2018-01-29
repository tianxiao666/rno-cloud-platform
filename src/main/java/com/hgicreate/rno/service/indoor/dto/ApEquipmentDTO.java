package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApEquipmentDTO {

  private Long apId;
  private String layerId;
  private String drawMapId;
  private String buildingId;
  private String floorName;
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
  private String positionX;
  private String positionY;
  private String note;
  private String status;
}
