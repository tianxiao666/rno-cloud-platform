package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_LTE_GRID_DESC")
public class LteGridDesc {
    @Id
    private Long id;
    private String filename;
    private String gridType;
    private Integer recordCount;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

    private Integer jobId;
    private Integer originFileId;

    private String createdUser;
    private Date createdDate;
}
