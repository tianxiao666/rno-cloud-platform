package com.hgicreate.rno.domain.gsm;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "RNO_GSM_DT_DATA")
public class GsmDtData {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DESCRIPTOR_ID")
    private GsmDtDesc gsmDtDesc;

    private Double longitute ;
    private Double latitude ;
    private Integer lac ;
    private Integer ci ;
    private String cell;
    private Integer bcch ;
    private Integer bsic ;
    private Integer rxlevbcch ;
    private Integer rxlevfull ;
    private Integer rxlevsub ;
    private Integer rxqualfull ;
    private Integer rxqualsub ;
    private Integer niBcch ;
    private Integer niBsic ;
    private Integer niRxlev ;
}
