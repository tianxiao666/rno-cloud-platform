package com.hgicreate.rno.web.rest.vm;

import lombok.Data;

@Data
public class LteNcellRelationQueryVM {
    private String cellName;
    private String ncellName;
    private String cellPci;
    private String ncellPci;
}
