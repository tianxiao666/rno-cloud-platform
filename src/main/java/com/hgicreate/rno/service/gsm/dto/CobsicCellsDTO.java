package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zeng.dh1
 */

@Data
public class CobsicCellsDTO {
    private Long bcch;
    private String bsic;
    private List<String> cells;
    private List<CobsicCellsExpandDTO> combinedCells;
}
