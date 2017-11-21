package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class LteTrafficData {

    @Id
    private Long id;

    private String areaName;
    private Date beginTime;
    private Date endTime;
    private String pmDn;
    private String dataType;
    private String vendor;
    private String recordCount;
    private Date createdDate;
    private String jobId;
}
