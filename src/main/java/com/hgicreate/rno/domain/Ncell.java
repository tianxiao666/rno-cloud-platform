package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RNO_LTE_NCELL_RELATION")
public class Ncell implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String cellId;
    private String ncellId;
}
