package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdealApMeaDataDTO implements Serializable{
  private static final long serialVersionUID = 1L;
  @Id
  private Long idealApId;
  private String buildingId;
  private String floorId;
  private String drawMapId;
  private String longitude;
  private String latitude;
  private String apLevels;
  private String planeX;
  private String planeY;
  private String meaDate;
  private String phoneDirection;
}
