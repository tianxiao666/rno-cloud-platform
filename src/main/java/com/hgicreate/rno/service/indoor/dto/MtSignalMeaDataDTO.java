package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

/**
 * @author chao.xj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MtSignalMeaDataDTO {

  @Id
  private String signalId;
  private String buildingId;
  private String floorId;
  private String drawMapId;
  private String longitude;
  private String latitude;
  private String signal;
  private String planeX;
  private String planeY;
  private String meaDate;
  private String signalType;
  private String deviceId;
}
