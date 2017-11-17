package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Data
@Entity
@Table(name = "RNO_LTE_DT_DESC")
public class DtDesc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long areaId;

    private String dataType;

    private String areaType;

    private Date createdDate;

    private String filename;

}
