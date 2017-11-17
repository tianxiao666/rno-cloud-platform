package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RNO_LTE_SCENE_GEO")
public class GeoScene implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "LteSceneGeoSeq")
    @SequenceGenerator(name = "LteSceneGeoSeq", sequenceName = "SEQ_LTE_SCENE_GEO", allocationSize = 1)
    private Long id;

    private String name;
    private double interrathoa2thdrsrp;
    private double interrathoa1thdrsrp;
    private double interrathoutranb1hyst;
    private double interrathoutranb1thdrscp;
    private double interrathoa1a2timetotrig;
    private double interrathoa1a2hyst;
    private double blindhoa1a2thdrsrp;
    private double interfreqhoa1a2timetotrig;
    private double a3interfreqhoa1thdrsrp;
    private double a3interfreqhoa2thdrsrp;
    private double interfreqhoa3offset;
    private double interfreqhoa1a2hyst;
    private double qrxlevmin;
    private double snonintrasearch;
    private double thrshservlow;
    private double treseleutran;
    private double cellreselpriority;

    @Override
    public String toString() {
        return "GeoScene{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", interrathoa2thdrsrp=" + interrathoa2thdrsrp +
                ", interrathoa1thdrsrp=" + interrathoa1thdrsrp +
                ", interrathoutranb1hyst=" + interrathoutranb1hyst +
                ", interrathoutranb1thdrscp=" + interrathoutranb1thdrscp +
                ", interrathoa1a2timetotrig=" + interrathoa1a2timetotrig +
                ", interrathoa1a2hyst=" + interrathoa1a2hyst +
                ", blindhoa1a2thdrsrp=" + blindhoa1a2thdrsrp +
                ", interfreqhoa1a2timetotrig=" + interfreqhoa1a2timetotrig +
                ", a3interfreqhoa1thdrsrp=" + a3interfreqhoa1thdrsrp +
                ", a3interfreqhoa2thdrsrp=" + a3interfreqhoa2thdrsrp +
                ", interfreqhoa3offset=" + interfreqhoa3offset +
                ", interfreqhoa1a2hyst=" + interfreqhoa1a2hyst +
                ", qrxlevmin=" + qrxlevmin +
                ", snonintrasearch=" + snonintrasearch +
                ", thrshservlow=" + thrshservlow +
                ", treseleutran=" + treseleutran +
                ", cellreselpriority=" + cellreselpriority +
                '}';
    }
}
