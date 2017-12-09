package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

@Data
public class CobsicCellsExpandDTO {
    private String combinedCell;
    private boolean whetherNcell;
    private String commonNcell;
    private boolean whetherComNcell;
    private double meaDis;
    private String mml;
}
