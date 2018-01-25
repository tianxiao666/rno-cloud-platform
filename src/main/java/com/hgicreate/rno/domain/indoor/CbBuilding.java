package com.hgicreate.rno.domain.indoor;


import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "INDOOR_CB_BUILDING")
public class CbBuilding {

  @Id
  @Column(name = "BUILDING_ID")
  @GeneratedValue(generator="CbBuildingSeq")
  @SequenceGenerator(name="CbBuildingSeq",sequenceName="SEQ_INDOOR_CB_BUILDING", allocationSize=1)
  private Long buildingId;
  @Column(name = "BUILDING_NAME")
  private String buildingName;
  private String district;
  private String postalcode;
  private String address;
  @Column(name = "BUILD_TYPE")
  private String buildType;
  @Column(name = "TOTAL_FLOOR")
  private String totalFloor;
  @Column(name = "PIC_ID")
  private String picId;
  private String phone;
  private String site;
  @Column(name = "LT_LONGITUDEL")
  private Double ltLongitudel;
  @Column(name = "LT_LATITUDEL")
  private Double ltLatitudel;
  @Column(name = "RB_LONGITUDEL")
  private Double rbLongitudel;
  @Column(name = "RB_LATITUDEL")
  private Double rbLatitudel;
  private String note;
  private String status;

  @OneToOne
  @JoinColumn(name = "city",referencedColumnName = "id")
  private com.hgicreate.rno.domain.Area area;
  @OneToOne
  @JoinColumn(name = "prov",referencedColumnName = "id")
  private Area area1;

  private enum BuildType{
    MALL_("大型商场"),OFFIC("写字楼"),LARGE("大型场馆"),TRAFF("交通枢纽");

    private String value;
    BuildType(String value) {
      this.value = value;
    }
    @Override
    public String toString() {
      return this.value;
    }
  }

  private enum BuildStatus{
    A("正常"),E("编辑中"),X("失效");

    private String value;
    BuildStatus(String value) {
      this.value = value;
    }
    @Override
    public String toString() {
      return this.value;
    }
  }
}
