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
@Table(name = "INDOOR_CB_PIC")
public class CbPic {

  @Id
  private Long picId;
  private String picType;
  private String buildingId;
  private String floorId;
  private String drawMapId;
  private String poiId;
  private String picTopic;
  private String picNote;
  private String mimeType;
  private String filesize;
  private String path;
  private String filename;
  private String status;
  private String createTime;
  private String modTime;
}
