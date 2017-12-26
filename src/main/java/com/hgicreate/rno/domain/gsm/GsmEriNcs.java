package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
@Entity
@Table(name = "RNO_GSM_ERI_NCS")
public class GsmEriNcs {
    @Id
    @GeneratedValue(generator = "GsmEriNcsSeq")
    @SequenceGenerator(name = "GsmEriNcsSeq", sequenceName = "SEQ_RNO_GSM_ERI_NCS", allocationSize = 1)
    @Column(name = "RNO_2G_ERI_NCS_ID")
    private Long rno2gEriNcsId;
    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private Area area;
    @Column(name = "RNO_2G_ERI_NCS_DESC_ID")
    private Long rno2gEriNcsDescId;
    private String cell;
    private String ncell;
    private Long chgr;
    private String bsic;
    private Long arfcn;
    private String definedNeighbour;
    private Long rectimearfcn;
    private Long reparfcn;
    private Long times;
    private Long navss;
    @Column(name = "times1")
    private Long times1;
    @Column(name = "navss1")
    private Long navss1;
    @Column(name = "times2")
    private Long times2;
    @Column(name = "navss2")
    private Long navss2;
    @Column(name = "times3")
    private Long times3;
    @Column(name = "navss3")
    private Long navss3;
    @Column(name = "times4")
    private Long times4;
    @Column(name = "navss4")
    private Long navss4;
    @Column(name = "times5")
    private Long times5;
    @Column(name = "navss5")
    private Long navss5;
    @Column(name = "times6")
    private Long times6;
    @Column(name = "navss6")
    private Long navss6;
    private Long timesrelss;
    @Column(name = "timesrelss2")
    private Long timesrelss2;
    @Column(name = "timesrelss3")
    private Long timesrelss3;
    @Column(name = "timesrelss4")
    private Long timesrelss4;
    @Column(name = "timesrelss5")
    private Long timesrelss5;
    private Long timesabss;
    private Long timesalone;
    private Long distance;
    private Long interfer;
    private Long caInterfer;
    private String ncells;
    private Long cellFreqCnt;
    private Long ncellFreqCnt;
    private Long sameFreqCnt;
    private Long adjFreqCnt;
    private Long ciDivider;
    private Long caDivider;
    private Date meaTime;
    private Long cellLon;
    private Long cellLat;
    private Long ncellLon;
    private Long ncellLat;
    private Long cellBearing;
    private Long cellDowntilt;
    private String cellSite;
    private String ncellSite;
    private String cellFreqSection;
    private String ncellFreqSection;
    private Long cellToNcellDir;
    private String cellBcch;
    private String cellTch;
    private String ncellTch;
    private String cellIndoor;
    private String ncellIndoor;
    private Long ncellBearing;
}
