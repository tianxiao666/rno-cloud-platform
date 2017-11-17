package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class DtNcellForDetail {

    @Id
    private Long id;

    private String ncellName;
    private Integer ncellRsrp;

}

