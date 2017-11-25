package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_LTE_TRAFFIC_DESC")
public class LteTrafficDesc {
    @Id
    private Long id;
    private String filename;
    private String dataType;
    private Integer recordCount;

    private Long areaId;

    private Integer jobId;
    private Integer originFileId;

    private String vendor;
    private Date beginTime;
    private Date endTime;
    private String createdUser;
    private Date createdDate;
}
