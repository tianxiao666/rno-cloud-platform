package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
public class LteTrafficDataForAnalysis {
    @Id
    private Long id;

    private String pmDn;
    private String pmUserLabel;
    private Float contextAttrelenb;
    private Float contextAttrelenbNormal;
    private Float contextSuccinitalsetup;
    private Float erabHofail;
    private Float erabHofail_1;
    private Float erabHofail_2;
    private Float erabHofail_3;
    private Float erabHofail_4;
    private Float erabHofail_5;
    private Float erabHofail_6;
    private Float erabHofail_7;
    private Float erabHofail_8;
    private Float erabHofail_9;
    private Float erabNbrattestab;
    private Float erabNbrattestab_1;
    private Float erabNbrattestab_2;
    private Float erabNbrattestab_3;
    private Float erabNbrattestab_4;
    private Float erabNbrattestab_5;
    private Float erabNbrattestab_6;
    private Float erabNbrattestab_7;
    private Float erabNbrattestab_8;
    private Float erabNbrattestab_9;
    private Float erabNbrhoinc_1;
    private Float erabNbrhoinc_2;
    private Float erabNbrhoinc_3;
    private Float erabNbrhoinc_4;
    private Float erabNbrhoinc_5;
    private Float erabNbrhoinc_6;
    private Float erabNbrhoinc_7;
    private Float erabNbrhoinc_8;
    private Float erabNbrhoinc_9;
    private Float erabNbrleft_1;
    private Float erabNbrleft_2;
    private Float erabNbrleft_3;
    private Float erabNbrleft_4;
    private Float erabNbrleft_5;
    private Float erabNbrleft_6;
    private Float erabNbrleft_7;
    private Float erabNbrleft_8;
    private Float erabNbrleft_9;
    private Float erabNbrreqrelenb;
    private Float erabNbrreqrelenb_1;
    private Float erabNbrreqrelenb_2;
    private Float erabNbrreqrelenb_3;
    private Float erabNbrreqrelenb_4;
    private Float erabNbrreqrelenb_5;
    private Float erabNbrreqrelenb_6;
    private Float erabNbrreqrelenb_7;
    private Float erabNbrreqrelenb_8;
    private Float erabNbrreqrelenb_9;
    private Float erabNbrreqrelenbNormal;
    private Float erabNbrreqrelenbNormal_1;
    private Float erabNbrreqrelenbNormal_2;
    private Float erabNbrreqrelenbNormal_3;
    private Float erabNbrreqrelenbNormal_4;
    private Float erabNbrreqrelenbNormal_5;
    private Float erabNbrreqrelenbNormal_6;
    private Float erabNbrreqrelenbNormal_7;
    private Float erabNbrreqrelenbNormal_8;
    private Float erabNbrreqrelenbNormal_9;
    private Float erabNbrsuccestab;
    private Float erabNbrsuccestab_1;
    private Float erabNbrsuccestab_2;
    private Float erabNbrsuccestab_3;
    private Float erabNbrsuccestab_4;
    private Float erabNbrsuccestab_5;
    private Float erabNbrsuccestab_6;
    private Float erabNbrsuccestab_7;
    private Float erabNbrsuccestab_8;
    private Float erabNbrsuccestab_9;
    private Float hoAttoutinterenbs1;
    private Float hoAttoutinterenbx2;
    private Float hoAttoutintraenb;
    private Float hoSuccoutinterenbs1;
    private Float hoSuccoutinterenbx2;
    private Float hoSuccoutintraenb;
    private Float pdcpUpoctdl;
    private Float pdcpUpoctdl_1;
    private Float pdcpUpoctdl_2;
    private Float pdcpUpoctdl_3;
    private Float pdcpUpoctdl_4;
    private Float pdcpUpoctdl_5;
    private Float pdcpUpoctdl_6;
    private Float pdcpUpoctdl_7;
    private Float pdcpUpoctdl_8;
    private Float pdcpUpoctdl_9;
    private Float pdcpUpoctul;
    private Float pdcpUpoctul_1;
    private Float pdcpUpoctul_2;
    private Float pdcpUpoctul_3;
    private Float pdcpUpoctul_4;
    private Float pdcpUpoctul_5;
    private Float pdcpUpoctul_6;
    private Float pdcpUpoctul_7;
    private Float pdcpUpoctul_8;
    private Float pdcpUpoctul_9;
    private Float rrcAttconnestab;
    private Float rrcAttconnreestab;
    private Float rrcSuccconnestab;
    private String cellId;

    private Date begin_time;
    private Date end_time;
    private Float erab_drop_celllevel;//E-RAB掉线率(小区级)
    private Float erab_estab_succ;//E-RAB建立成功率
    private Float rrc_conn_estab_succ;//RRC连接建立成功率
    private Float wire_conn;//无线接通率
    private Float wire_drop_celllevel;//无线掉线率(小区级)
    private Float switch_succ;//切换成功率
    private Float rrc_conn_rebuild;//RRC连接重建比率

}
