package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_LTE_HO_DESC")
public class LteHoDesc {
    @Id
    private Long id;
    private String filename;
    private String vendor;
    private Date recordDate;
    private Integer recordCount;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

    private Integer jobId;
    private Integer originFileId;

    private String createdUser;
    private Date createdDate;
}
