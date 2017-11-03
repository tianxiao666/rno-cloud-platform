package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RNO_SYS_AREA")
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String name;
    private int areaLevel;
    private long parentId;
    private double longitude;
    private double latitude;
}
