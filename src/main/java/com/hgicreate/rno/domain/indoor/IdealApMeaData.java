package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author chao.xj
 */
@Data
@Entity
@Table(name = "INDOOR_CB_IDEAL_AP_MEA_DATA")
public class IdealApMeaData implements Serializable{

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long idealApId;
  private String buildingId;
  private String floorId;
  private String drawMapId;
  private String longitude;
  private String latitude;
  private String apLevels;
  @Column(name = "PLANE_X")
  private String planeX;
  @Column(name = "PLANE_Y")
  private String planeY;
  private String meaDate;
  private String phoneDirection;
}
