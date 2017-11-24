package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author ke_weixu
 */
@Data
@Entity
@Table(name = "RNO_LTE_SCENE_TIME")
public class TimeScene implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "LteSceneTimeSeq")
    @SequenceGenerator(name = "LteSceneTimeSeq", sequenceName = "SEQ_LTE_SCENE_TIME", allocationSize = 1)
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
}
