package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tao.xj
 */
@Data
@Entity
@Table(name = "RNO_GSM_INTERFER_MATRIX_JOB")
public class GsmInterferMatrixJob {
    @Id
    @GeneratedValue(generator = "InterferMatrixJobSeq")
    @SequenceGenerator(name = "InterferMatrixJobSeq", sequenceName = "SEQ_GSM_INTERFER_MATRIX_JOB", allocationSize = 1)
    private Long id;

    private String name;
    private Integer priority;
    private String dataType;
    private Date begMeaTime;
    private Date endMeaTime;
    private Date startTime;
    private Date completeTime;
    private String status;
    private String createdUser;
    private Date createdDate;
    private Long recordCount;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;
}
