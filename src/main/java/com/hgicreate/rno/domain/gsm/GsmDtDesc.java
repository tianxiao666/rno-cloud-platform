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
@Table(name = "RNO_GSM_DT_DESCRIPTOR")
public class GsmDtDesc {

    @Id
    @GeneratedValue(generator = "GsmDtDescSeq")
    @SequenceGenerator(name = "GsmDtDescSeq", sequenceName = "SEQ_RNO_GSM_DT_DESCRIPTOR", allocationSize = 1)
    private Long dtDescId;
    private String name;
    private String dtlogVersion;
    private String vendor;
    private String device;
    private String version;
    private String netMode;
    private String type;
    private Date testDate;
    private Date createTime;
    private Date modTime;
    private String status;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;
}
