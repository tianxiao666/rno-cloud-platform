package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.DataJob;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author tao.xj
 */
@Data
@Entity
@Table(name = "RNO_GSM_STRUCANA_JOB_REPORT")
public class GsmStructJobReport implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructJobReportSeq")
    @SequenceGenerator(name = "StructJobReportSeq", sequenceName = "SEQ_GSM_STRUCANA_JOB_REPORT", allocationSize = 1)
    private Long id;

    private String stage;
    private Date startTime;
    private Date completeTime;
    private String status;
    private String message;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private GsmStructAnalysisJob gsmStructAnalysisJob;
}
