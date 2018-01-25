package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "INDOOR_DM_DRAW_MAP")
public class DmDrawMap {

  @Id
  @Column(name = "DRAW_MAP_ID")
  private Long drawMapId;
  private Long buildingId;
  private String floorId;
  @Column(name = "DM_TOPIC")
  private String dmTopic;
  private String dwScale;
  private String dwUnit;
  private String height;
  private String width;
  private String picId;
  private String dmNote;
  private String status;
  @Temporal(TemporalType.TIMESTAMP)
  private Date modTime;

  @OneToOne
  @JoinColumn(name = "floorId",insertable = false,updatable = false)
  private CbFloor cbFloor;
}
