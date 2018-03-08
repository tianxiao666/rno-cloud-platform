package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author chao.xj
 */
@Data
@Entity
@Table(name = "INDOOR_DM_LAYER_ELEMENT")
public class DmLayerElement {

  @Id
  @Column(name = "ELEMENT_ID")
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
  @Column(name = "POSITION_X")
  private String positionX;
  @Column(name = "POSITION_Y")
  private String positionY;
  private String status;
  private String elementText;
}
