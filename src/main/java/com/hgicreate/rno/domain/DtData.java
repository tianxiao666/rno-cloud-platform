package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "RNO_LTE_DT_DATA")
public class DtData implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private Long descId;
    private java.sql.Timestamp metaTime;
    private Double longitude;
    private Double latitude;
    private Integer tac;
    private String scellId;
    private Integer earfcn;
    private Integer pci;
    private Integer rsrp;
    private Integer rsSinr;
    private Double scellDist;
    private Integer scellAzimuthDiff;

}

