package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_GSM_STRUCANA_JOB")
public class GsmStructAnalysisJob {
    @Id
    @GeneratedValue(generator = "StructAnaJobSeq")
    @SequenceGenerator(name = "StructAnaJobSeq", sequenceName = "SEQ_RNO_GSM_STRUCANA_JOB", allocationSize = 1)
    private Long id;
    private String name;
    private String description;
    private Integer priority;
    private Date begMeaTime;
    private Date endMeaTime;
    private Date startTime;
    private Date completeTime;
    private String status;
    private String resultStorePath;
    private String createdUser;
    private Date createdDate;
    private Integer fileNumber;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;
}
