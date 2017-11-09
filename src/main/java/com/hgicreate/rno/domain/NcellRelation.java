package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class NcellRelation {
    @Id
    private long id;

    private String cellId;
    private String cellName;
    private String ncellId;
    private String ncellName;
    private String cellEnodebId;
    private String ncellEnodebId;
    private String cellPci;
    private String ncellPci;
}
