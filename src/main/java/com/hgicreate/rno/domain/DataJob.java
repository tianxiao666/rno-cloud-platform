package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "RNO_DATA_JOB")
public class DataJob {

    @Id
    private Long id;

    private String name;
    private String type;
    private Integer priority;
    private String createdUser;
    private LocalDateTime createdDate;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private String status;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

    @OneToOne
    @JoinColumn(name = "origin_file_id", referencedColumnName = "id")
    private OriginFile originFile;

}
