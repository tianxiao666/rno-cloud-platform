package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author chao.xj
 */
@Data
@Entity
@Table(name = "INDOOR_CB_POI_ATTR")
public class CbPoiAttr {

  @Id
  private Long poiId;
  private String poiKey;
  private String poiValue;
  private String createTime;
  private String modTime;
}
