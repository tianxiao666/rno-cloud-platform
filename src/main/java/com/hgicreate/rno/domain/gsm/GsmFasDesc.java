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
@Table(name = "RNO_GSM_FAS_DESC")
public class GsmFasDesc {
    @Id
    @GeneratedValue(generator = "FasDescSeq")
    @SequenceGenerator(name = "FasDescSeq", sequenceName = "SEQ_RNO_GSM_FAS_DESC", allocationSize = 1)
    private Long fasDescId;
    @OneToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private Area area;
    private Date meaTime;
    private String bsc;
    private Long recordNum;
    private Date createTime;
}
