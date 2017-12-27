package com.hgicreate.rno.domain.gsm;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_GSM_DT_SAMPLE")
public class GsmDtSample {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "DATA_ID")
    private GsmDtData gsmDtData;

    private Date sampleTime ;
    private Double longitude ;
    private Double latitude ;
    private Integer altitude ;
    private Integer speed ;
    private String mapLnglat;
    private Integer lac ;
    private Integer ci ;
    private String cell;
    private String cellIndoor;
    private Integer cellLongitude ;
    private Integer cellLatitude ;
    private Integer rxlevsub ;
    private Integer rxqualsub ;
    private Integer distance ;
    private Integer ncellAvgRxlev ;
    private Integer angle ;
    private Integer ncellCount ;
    @Column(name = "ncell_bcch_1")
    private Integer ncellBcchOne ;
    @Column(name = "ncell_bsic_1")
    private String ncellBsicOne ;
    @Column(name = "ncell_rxlev_1")
    private Integer ncellRxlevOne ;
    @Column(name = "ncell_name_1")
    private String ncellNameOne ;
    @Column(name = "ncell_indoor_1")
    private String ncellIndoorOne ;
    @Column(name = "ncell_bcch_2")
    private Integer ncellBcchTwo ;
    @Column(name = "ncell_bsic_2")
    private String ncellBsicTwo ;
    @Column(name = "ncell_rxlev_2")
    private Integer ncellRxlevTwo ;
    @Column(name = "ncell_name_2")
    private String ncellNameTwo ;
    @Column(name = "ncell_indoor_2")
    private String ncellIndoorTwo ;
    @Column(name = "ncell_bcch_3")
    private Integer ncellBcchThree ;
    @Column(name = "ncell_bsic_3")
    private String ncellBsicThree ;
    @Column(name = "ncell_rxlev_3")
    private Integer ncellRxlevThree ;
    @Column(name = "ncell_name_3")
    private String ncellNameThree ;
    @Column(name = "ncell_indoor_3")
    private String ncellIndoorThree ;
    @Column(name = "ncell_bcch_4")
    private Integer ncellBcchFour ;
    @Column(name = "ncell_bsic_4")
    private String ncellBsicFour ;
    @Column(name = "ncell_rxlev_4")
    private Integer ncellRxlevFour ;
    @Column(name = "ncell_name_4")
    private String ncellNameFour ;
    @Column(name = "ncell_indoor_4")
    private String ncellIndoorFour ;
    @Column(name = "ncell_bcch_5")
    private Integer ncellBcchFive ;
    @Column(name = "ncell_bsic_5")
    private String ncellBsicFive ;
    @Column(name = "ncell_rxlev_5")
    private Integer ncellRxlevFive ;
    @Column(name = "ncell_name_5")
    private String ncellNameFive ;
    @Column(name = "ncell_indoor_5")
    private String ncellIndoorFive ;
    @Column(name = "ncell_bcch_6")
    private Integer ncellBcchSix ;
    @Column(name = "ncell_bsic_6")
    private String ncellBsicSix ;
    @Column(name = "ncell_rxlev_6")
    private Integer ncellRxlevSix ;
    @Column(name = "ncell_name_6")
    private String ncellNameSix ;
    @Column(name = "ncell_indoor_6")
    private String ncellIndoorSix;
}
