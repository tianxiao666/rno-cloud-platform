package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LteNcellRelationDTO {
    private Long id;
    private String cellId;
    private String cellName;
    private String ncellId;
    private String ncellName;
    private String cellEnodebId;
    private String ncellEnodebId;
    private String cellPci;
    private String ncellPci;
}
