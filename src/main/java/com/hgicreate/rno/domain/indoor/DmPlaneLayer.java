package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "INDOOR_DM_PLANE_LAYER")
public class DmPlaneLayer {

  @Id
  @Column(name = "LAYER_ID")
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
