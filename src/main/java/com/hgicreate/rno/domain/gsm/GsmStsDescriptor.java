package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;
import org.apache.ibatis.annotations.One;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_GSM_STS_DESCRIPTOR")
public class GsmStsDescriptor implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StsDescriptorSeq")
    @SequenceGenerator(name = "StsDescriptorSeq", sequenceName = "SEQ_GSM_STS_DESCRIPTOR", allocationSize = 1)
    private Long stsDescId;
    private String netType;
    private String specType;
    private Date stsDate;
    private String stsPeriod;
    private Date createTime;
    private Date modTime;
    private String status;

    @OneToOne
    @JoinColumn(name = "area_id",referencedColumnName = "id")
    private Area area;
}
