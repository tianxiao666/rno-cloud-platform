package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_GSM_CELL")
public class GsmCell implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String cellId;
    private String cellName;
    private String enName;
    private String coverArea;
    private String bsc;
    private Integer lac;
    private String bts;
    private String btsType;
    private Integer ci;
    private Float longitude;
    private Float latitude;
    private Integer azimuth;
    private Integer bcch;
    private Integer bsic;
    private Integer mDowntilt;
    private Integer eDowntilt;
    private Integer totalDowntilt;
    private Integer antennaHeight;
    private String coverType;
    private Integer carrierFreq;
    private String freqHoppingType;
    private String tch;
    private Integer tchTotal;
    private String createdUser;
    private String lastModifiedUser;
    private Date createdDate;
    private Date lastModifiedDate;
    private Long descId;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;
}
