package com.hgicreate.rno.service.gsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GsmNcellRelationDTO {
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
