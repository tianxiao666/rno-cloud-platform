package com.hgicreate.rno.domain;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_LTE_KPI_DATA")
public class LteKpiData implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String cellId;
    private String cellName;
    private Double longitude;
    private Double latidude;
    private Integer lteScAoa;
    private Integer lteScTadv;
    private Integer lteScEarfcn;
    private Integer lteScRsrp;
    private Integer lteScPhr;
    private Integer lteScSinrUl;
    private Integer lteNcEarfcn;
    private Integer lteNcRsrp;
    private Integer lteNcPci;
    private Integer overlapNcCount;
    private Integer totalNcCount;
    private Integer originalAoa;
    private Integer originalTadv;
    private Integer mmeUeS1apId;
    private Date timeStamp;

    @OneToOne
    @JoinColumn(name = "desc_id", referencedColumnName = "id")
    private LteKpiDesc lteKpiDesc;
}
