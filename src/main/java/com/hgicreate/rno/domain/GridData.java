package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name="RNO_LTE_GRID_DATA")
public class GridData implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private Long areaId;
    private String gridType;
    private String gridCode;
    private String centerCoord;
    private Long descId;

}
