package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_LTE_DT_DESC")
public class DtDesc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

   // private Long areaId;

    private String dataType;

    private String areaType;

    private Date createdDate;

    private String filename;

    private Integer recordCount;
    private Long jobId;
    private Long originFileId;
    private String createdUser;
    private String importFlag;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

}
