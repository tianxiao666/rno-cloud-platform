package com.hgicreate.rno.domain.gsm;

import lombok.Data;

import javax.persistence.*;

/**
 * @author tao.xj
 */
@Data
@Entity
@Table(name = "RNO_GSM_STRUCANA_JOB_PARAM")
public class GsmStructJobParam {
    @Id
    @GeneratedValue(generator = "GsmStructJobParamSeq")
    @SequenceGenerator(name = "GsmStructJobParamSeq", sequenceName = "SEQ_GSM_STRUCANA_JOB_PARAM", allocationSize = 1)
    private Long id;

    private String paramType;
    private String paramCode;
    private String paramVal;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private GsmStructAnalysisJob gsmStructAnalysisJob;
}
