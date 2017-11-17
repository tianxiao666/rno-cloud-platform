package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "RNO_DATA_JOB_REPORT")
public class DataJobReport {

    @Id
    private Long id;

    private String stage;
    private Date startTime;
    private Date completeTime;
    private String status;
    private String message;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private DataJob dataJob;
}
