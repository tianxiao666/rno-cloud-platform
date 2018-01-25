package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DmLayerElementDTO {

  private Long elementId;
  private String layerId;
  private String drawMapId;
  private String floorId;
  private String buildingId;
  private String svgId;
  private String elementTopic;
  private String elementType;
  private String poiType;
  private String poiId;
  private String positionX;
  private String positionY;
  private String status;
  private String elementText;
  private String createTime;
  private String modTime;
}
