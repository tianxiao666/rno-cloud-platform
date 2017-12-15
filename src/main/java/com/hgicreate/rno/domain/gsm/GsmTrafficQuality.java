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
@Table(name = "RNO_GSM_TRAFFIC_QUALITY")
public class GsmTrafficQuality {
    @Id
    @GeneratedValue(generator = "GsmTrafficQualitySeq")
    @SequenceGenerator(name = "GsmTrafficQualitySeq", sequenceName = "SEQ_RNO_GSM_TRAFFIC_QUALITY", allocationSize = 1)
    private Long id;
    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;
    private Integer type;
    private Date staticTime;
    private Double score;
    private String indexClass;
    private String indexName;
    private Long indexValue;
}
