package com.hgicreate.rno.domain.gsm;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_GSM_CELL_DESC")
public class GsmCellDesc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String filename;
    private String dataType;
    private Integer recordCount;

    @OneToOne
    @JoinColumn(name = "area_id", referencedColumnName = "id")
    private Area area;

    private Integer jobId;
    private Integer originFileId;

    private String createdUser;
    private Date createdDate;
}
