package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chao.xj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DmPlaneLayerDTO {

  private Long layerId;
  private String drawMapId;
  private String buildingId;
  private String floorId;
  private String layerTopic;
  private String layerNote;
  private String layerType;
  private String lOrder;
  private String status;
}
