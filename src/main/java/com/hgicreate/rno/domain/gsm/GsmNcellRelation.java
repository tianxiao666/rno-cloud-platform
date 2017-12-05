package com.hgicreate.rno.domain.gsm;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "RNO_GSM_NCELL_RELATION")
public class GsmNcellRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String cellId;
    private String ncellId;
    private String createdUser;
    private Date createdDate;
    private Long descId;
}
