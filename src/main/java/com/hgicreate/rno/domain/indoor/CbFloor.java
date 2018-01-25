package com.hgicreate.rno.domain.indoor;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "INDOOR_CB_FLOOR")
public class CbFloor implements Serializable{

  @Id
  @Column(name = "FLOOR_ID")
  @GeneratedValue(generator="CbFloorSeq")
  @SequenceGenerator(name="CbFloorSeq",sequenceName="SEQ_INDOOR_CB_FLOOR", allocationSize=1)
  private Long floorId;
  @Column(name = "BUILDING_ID")
  private Long buildingId;
  @Column(name = "PIC_ID")
  private String picId;
  @Column(name = "FLOOR_NAME")
  private String floorName;
  @Column(name = "PHYSICAL_FLOOR")
  private String physicalFloor;
  private String basement;
  @Column(name = "FLOOR_TYPE")
  private String floorType;
  @Column(name = "FLOOR_NOTE")
  private String floorNote;
  private String status;

  private enum FloorType{
    LOOBY("大堂"),FUEST("客房"),EFOOD("餐饮");

    private String value;
    FloorType(String value) {
      this.value = value;
    }
    @Override
    public String toString() {
      return this.value;
    }
  }

  private enum FloorStatus{
    A("正常"),X("失效");

    private String value;
    FloorStatus(String value) {
      this.value = value;
    }
    @Override
    public String toString() {
      return this.value;
    }
  }
}
