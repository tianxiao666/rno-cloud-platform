package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RNO_LTE_TRAFFIC_DATA")
public class LteTrafficData {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "desc_id")
    private LteTrafficDesc lteTrafficDesc;

    private String pmDn;
    private String pmUserLabel;
    private String contextAttrelenb;
    private String contextAttrelenbNormal;
    private String contextSuccinitalsetup;
    private String erabHofail;
    private String erabHofail_1;
    private String erabHofail_2;
    private String erabHofail_3;
    private String erabHofail_4;
    private String erabHofail_5;
    private String erabHofail_6;
    private String erabHofail_7;
    private String erabHofail_8;
    private String erabHofail_9;
    private String erabNbrattestab;
    private String erabNbrattestab_1;
    private String erabNbrattestab_2;
    private String erabNbrattestab_3;
    private String erabNbrattestab_4;
    private String erabNbrattestab_5;
    private String erabNbrattestab_6;
    private String erabNbrattestab_7;
    private String erabNbrattestab_8;
    private String erabNbrattestab_9;
    private String erabNbrhoinc_1;
    private String erabNbrhoinc_2;
    private String erabNbrhoinc_3;
    private String erabNbrhoinc_4;
    private String erabNbrhoinc_5;
    private String erabNbrhoinc_6;
    private String erabNbrhoinc_7;
    private String erabNbrhoinc_8;
    private String erabNbrhoinc_9;
    private String erabNbrleft_1;
    private String erabNbrleft_2;
    private String erabNbrleft_3;
    private String erabNbrleft_4;
    private String erabNbrleft_5;
    private String erabNbrleft_6;
    private String erabNbrleft_7;
    private String erabNbrleft_8;
    private String erabNbrleft_9;
    private String erabNbrreqrelenb;
    private String erabNbrreqrelenb_1;
    private String erabNbrreqrelenb_2;
    private String erabNbrreqrelenb_3;
    private String erabNbrreqrelenb_4;
    private String erabNbrreqrelenb_5;
    private String erabNbrreqrelenb_6;
    private String erabNbrreqrelenb_7;
    private String erabNbrreqrelenb_8;
    private String erabNbrreqrelenb_9;
    private String erabNbrreqrelenbNormal;
    private String erabNbrreqrelenbNormal_1;
    private String erabNbrreqrelenbNormal_2;
    private String erabNbrreqrelenbNormal_3;
    private String erabNbrreqrelenbNormal_4;
    private String erabNbrreqrelenbNormal_5;
    private String erabNbrreqrelenbNormal_6;
    private String erabNbrreqrelenbNormal_7;
    private String erabNbrreqrelenbNormal_8;
    private String erabNbrreqrelenbNormal_9;
    private String erabNbrsuccestab;
    private String erabNbrsuccestab_1;
    private String erabNbrsuccestab_2;
    private String erabNbrsuccestab_3;
    private String erabNbrsuccestab_4;
    private String erabNbrsuccestab_5;
    private String erabNbrsuccestab_6;
    private String erabNbrsuccestab_7;
    private String erabNbrsuccestab_8;
    private String erabNbrsuccestab_9;
    private String hoAttoutinterenbs1;
    private String hoAttoutinterenbx2;
    private String hoAttoutintraenb;
    private String hoSuccoutinterenbs1;
    private String hoSuccoutinterenbx2;
    private String hoSuccoutintraenb;
    private String pdcpUpoctdl;
    private String pdcpUpoctdl_1;
    private String pdcpUpoctdl_2;
    private String pdcpUpoctdl_3;
    private String pdcpUpoctdl_4;
    private String pdcpUpoctdl_5;
    private String pdcpUpoctdl_6;
    private String pdcpUpoctdl_7;
    private String pdcpUpoctdl_8;
    private String pdcpUpoctdl_9;
    private String pdcpUpoctul;
    private String pdcpUpoctul_1;
    private String pdcpUpoctul_2;
    private String pdcpUpoctul_3;
    private String pdcpUpoctul_4;
    private String pdcpUpoctul_5;
    private String pdcpUpoctul_6;
    private String pdcpUpoctul_7;
    private String pdcpUpoctul_8;
    private String pdcpUpoctul_9;
    private String rrcAttconnestab;
    private String rrcAttconnreestab;
    private String rrcSuccconnestab;
    private String cellId;
}
