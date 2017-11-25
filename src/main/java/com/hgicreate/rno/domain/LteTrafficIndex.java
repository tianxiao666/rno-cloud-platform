package com.hgicreate.rno.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "RNO_LTE_TRAFFIC_INDEX")
public class LteTrafficIndex {
    @Id
    private Integer indexDescId;

    private String indexRealName;
    private String indexDisplayName;
    private String indexCalFormula;
    private Integer indexOrder;

}
