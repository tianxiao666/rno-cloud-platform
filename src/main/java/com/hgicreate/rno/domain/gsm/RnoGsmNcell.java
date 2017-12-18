package com.hgicreate.rno.domain.gsm;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "RNO_NCELL")
public class RnoGsmNcell implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    private Long ncellId;
    private String cell;
    private String ncell;
    private String cs;
    private String dir;
    private Long bscId;

}
