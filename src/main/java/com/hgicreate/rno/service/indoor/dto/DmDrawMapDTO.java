package com.hgicreate.rno.service.indoor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DmDrawMapDTO {

  private Long drawMapId;
  private Long buildingId;
  private String floorId;
  private String floorName;
  private String dmTopic;
  private String dwScale;
  private String dwUnit;
  private String height;
  private String width;
  private String picId;
  private String dmNote;
  private String status;
}
