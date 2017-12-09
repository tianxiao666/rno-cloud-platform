package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.List;

@Data
public class CobsicCellsDTO {
    private long bcch;
    private String bsic;
    private List<String> cells;
    private List<CobsicCellsExpandDTO> combinedCells;
}
