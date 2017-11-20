package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RNO_LTE_TRAFFIC_DATA")
public class TrafficData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String pmDn;
    private String pmUserLabel;
    private Integer contextAttrelenb;
    private Integer contextAttrelenbNormal;
    private Integer contextSuccinitalsetup;
    private Integer erabHofail;
    private Integer erabHofail1;
    private Integer erabHofail2;
    private Integer erabHofail3;
    private Integer erabHofail4;
    private Integer erabHofail5;
    private Integer erabHofail6;
    private Integer erabHofail7;
    private Integer erabHofail8;
    private Integer erabHofail9;
    private Integer erabNbrattestab;
    private Integer erabNbrattestab1;
    private Integer erabNbrattestab2;
    private Integer erabNbrattestab3;
    private Integer erabNbrattestab4;
    private Integer erabNbrattestab5;
    private Integer erabNbrattestab6;
    private Integer erabNbrattestab7;
    private Integer erabNbrattestab8;
    private Integer erabNbrattestab9;
    private Integer erabNbrhoinc1;
    private Integer erabNbrhoinc2;
    private Integer erabNbrhoinc3;
    private Integer erabNbrhoinc4;
    private Integer erabNbrhoinc5;
    private Integer erabNbrhoinc6;
    private Integer erabNbrhoinc7;
    private Integer erabNbrhoinc8;
    private Integer erabNbrhoinc9;
    private Integer erabNbrleft1;
    private Integer erabNbrleft2;
    private Integer erabNbrleft3;
    private Integer erabNbrleft4;
    private Integer erabNbrleft5;
    private Integer erabNbrleft6;
    private Integer erabNbrleft7;
    private Integer erabNbrleft8;
    private Integer erabNbrleft9;
    private Integer erabNbrreqrelenb;
    private Integer erabNbrreqrelenb1;
    private Integer erabNbrreqrelenb2;
    private Integer erabNbrreqrelenb3;
    private Integer erabNbrreqrelenb4;
    private Integer erabNbrreqrelenb5;
    private Integer erabNbrreqrelenb6;
    private Integer erabNbrreqrelenb7;
    private Integer erabNbrreqrelenb8;
    private Integer erabNbrreqrelenb9;
    private Integer erabNbrreqrelenbNormal;
    private Integer erabNbrreqrelenbNormal1;
    private Integer erabNbrreqrelenbNormal2;
    private Integer erabNbrreqrelenbNormal3;
    private Integer erabNbrreqrelenbNormal4;
    private Integer erabNbrreqrelenbNormal5;
    private Integer erabNbrreqrelenbNormal6;
    private Integer erabNbrreqrelenbNormal7;
    private Integer erabNbrreqrelenbNormal8;
    private Integer erabNbrreqrelenbNormal9;
    private Integer erabNbrsuccestab;
    private Integer erabNbrsuccestab1;
    private Integer erabNbrsuccestab3;
    private Integer erabNbrsuccestab4;
    private Integer erabNbrsuccestab5;
    private Integer erabNbrsuccestab6;
    private Integer erabNbrsuccestab7;
    private Integer erabNbrsuccestab8;
    private Integer erabNbrsuccestab9;
    private Integer hoAttoutinterenbs1;
    private Integer hoAttoutinterenbx2;
    private Integer hoAttoutintraenb;
    private Integer hoSuccoutinterenbs1;
    private Integer hoSuccoutinterenbx2;
    private Integer hoSuccoutintraenb;
    private Integer pdcpUpoctdl;
    private Integer pdcpUpoctdl1;
    private Integer pdcpUpoctdl2;
    private Integer pdcpUpoctdl3;
    private Integer pdcpUpoctdl4;
    private Integer pdcpUpoctdl5;
    private Integer pdcpUpoctdl6;
    private Integer pdcpUpoctdl7;
    private Integer pdcpUpoctdl8;
    private Integer pdcpUpoctdl9;
    private Integer pdcpUpoctul;
    private Integer pdcpUpoctul1;
    private Integer pdcpUpoctul2;
    private Integer pdcpUpoctul3;
    private Integer pdcpUpoctul4;
    private Integer pdcpUpoctul5;
    private Integer pdcpUpoctul6;
    private Integer pdcpUpoctul7;
    private Integer pdcpUpoctul8;
    private Integer pdcpUpoctul9;
    private Integer rrcAttconnestab;
    private Integer rrcAttconnreestab;
    private Integer rrcSuccconnestab;

}
