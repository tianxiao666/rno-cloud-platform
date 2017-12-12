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
@Table(name = "RNO_GSM_HW_NCS_DESC")
public class GsmHwNcsDesc {
    @Id
    @GeneratedValue(generator = "HwNcsDescSeq")
    @SequenceGenerator(name = "HwNcsDescSeq", sequenceName = "SEQ_RNO_GSM_HW_NCS_DESC", allocationSize = 1)
    private Long id;

    private Date meaTime;

    private String bsc;
    @OneToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private Area area;
    private Long recordCount;
    private String status;
    private Date createTime;
    private Date modTime;
}
