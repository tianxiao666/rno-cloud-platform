package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_DATA_JOB")
public class DataJob implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DataJobSeq")
    @SequenceGenerator(name = "DataJobSeq", sequenceName = "SEQ_DATA_JOB", allocationSize = 1)
    private Long id;

    private String name;
    private String type;
    private Integer priority;
    private String createdUser;
    private Date createdDate;
    private Date startTime;
    private Date completeTime;
    private String status;
    private String dataStoreType;
    private String dataStorePath;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

    @OneToOne
    @JoinColumn(name = "origin_file_id", referencedColumnName = "id")
    private OriginFile originFile;

}
