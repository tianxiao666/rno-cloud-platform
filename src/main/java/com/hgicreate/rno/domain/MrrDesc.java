package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
@Entity
@Table(name = "RNO_GSM_MRR_DESC")
public class MrrDesc {

    @Id
    @GeneratedValue(generator = "MrrDescSeq")
    @SequenceGenerator(name = "MrrDescSeq", sequenceName = "SEQ_RNO_GSM_MRR_DESC", allocationSize = 1)
    private Long id;
    private Date meaDate;
    private String fileName;
    private String bsc;
    private String cityName;
    private String factory;

}
