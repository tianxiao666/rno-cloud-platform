package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tao.xj
 */
@Data
@Entity
@Table(name = "RNO_GSM_NCELL_DESC")
public class GsmNcellDesc {
    @Id
    private Long id;

    private String filename;
    private String dataType;
    private Integer recordCount;
    private String createdUser;
    private Date createdDate;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

    private Integer jobId;
    private Integer originFileId;
}
