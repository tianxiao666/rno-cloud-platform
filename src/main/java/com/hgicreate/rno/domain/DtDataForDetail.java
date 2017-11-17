package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity
public class DtDataForDetail {

    @Id
    private Long id;

    private Timestamp metaTime;
    private String cellName;
    private Integer earfcn;
    private Integer pci;
    private Integer rsrp;
    private Integer rsSinr;
    private Double scellDist;
    private Double longitude;
    private Double latitude;

    private String areaType;

}

