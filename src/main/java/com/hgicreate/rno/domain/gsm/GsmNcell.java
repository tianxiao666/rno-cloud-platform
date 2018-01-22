package com.hgicreate.rno.domain.gsm;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author taao.xj
 */
@Data
@Entity
public class GsmNcell {
    @Id
    private Long id;

    private String cellId;
    private String cellName;
    private String ncellId;
    private String ncellName;
    private String cellEnName;
    private String ncellEnName;
    private String cellBsc;
    private String ncellBsc;
}
