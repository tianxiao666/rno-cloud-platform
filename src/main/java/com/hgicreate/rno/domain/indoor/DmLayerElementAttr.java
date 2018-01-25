package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "INDOOR_DM_LAYER_ELEMENT_ATTR")
@IdClass(DmLayerElementAttrPk.class)
public class DmLayerElementAttr implements Serializable{

  @Id
  private Long elementId;
  private String layerId;
  private String drawMapId;
  @Id
  private String attrName;
  private String attrValue;
}
