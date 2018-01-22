package com.hgicreate.rno.domain;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
@Entity
@Table(name = "RNO_GSM_NEW_STATION_DESC")
public class NewStationDesc {
    @Id
    @GeneratedValue(generator = "GsmNewStationDescSeq")
    @SequenceGenerator(name = "GsmNewStationDescSeq", sequenceName = "SEQ_RNO_GSM_NEW_STATION_DESC", allocationSize = 1)
    private Long id;

    private String filename;
    private String fileCode;
    private Long recordCount;
    private Long jobId;
    private Long originFileId;
    private String createdUser;
    private Date createdDate;
    private Date testTime;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;
}
