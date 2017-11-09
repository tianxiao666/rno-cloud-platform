package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NcellRelationDTO {
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
