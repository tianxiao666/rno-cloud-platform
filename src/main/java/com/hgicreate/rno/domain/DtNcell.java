package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RNO_LTE_DT_NCELL")
public class DtNcell implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private Long dataId;
    private Integer earfcn;
    private Integer pci;
    private Integer ncellOrder;
    private String ncellId;
    private Integer rsrp;

}

