package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RNO_GSM_BSC_DATA")
public class GsmBscData implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "BscDataSeq")
    @SequenceGenerator(name = "BscDataSeq", sequenceName = "SEQ_GSM_BSC_DATA", allocationSize = 1)
    private Long id;

    private String bsc;
    private String vendor;
    private Long descId;
    private String status;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;


}
