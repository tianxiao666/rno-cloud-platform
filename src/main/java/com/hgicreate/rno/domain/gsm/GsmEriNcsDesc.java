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
@Table(name = "RNO_GSM_ERI_NCS_DESCRIPTOR")
public class GsmEriNcsDesc {
    @Id
    @GeneratedValue(generator = "EriNcsDescSeq")
    @SequenceGenerator(name = "EriNcsDescSeq", sequenceName = "SEQ_RNO_GSM_ERI_NCS_DESCRIPTOR", allocationSize = 1)
    private Long rno_2gEriNcsDescId;
    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;
    private String name;
    private String bsc;
    private String freqSection;
    private Date meaTime;
    private Long recordCount;
    private Long segtime;
    private Long relssn;
    private Long abss;
    private Long numfreq;
    private Long rectime;
    private String netType;
    private String vendor;
    private Date createTime;
    private Date modTime;
    private String status;
    private Long fileFormat;
    private String rid;
    private Long termReason;
    private Long ecnoabss;
    private Long relssSign;
    private Long relss;
    private Long relss2_sign;
    private Long relss2;
    private Long relss3_sign;
    private Long relss3;
    private Long relss4_sign;
    private Long relss4;
    private Long relss5_sign;
    private Long relss5;
    private Long ncelltype;
    private Long nucelltype;
    private Long tfddmrr;
    private Long numumfi;
    private Long tnccpermIndicator;
    private Long tnccpermBitmap;
    private Long tmbcr;
    private Long cityId;
}
