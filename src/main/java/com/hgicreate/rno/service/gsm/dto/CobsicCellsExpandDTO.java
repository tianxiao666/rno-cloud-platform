package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

@Data
public class CobsicCellsExpandDTO {
    private String combinedCell;
    private Boolean whetherNcell;
    private String commonNcell;
    private Boolean whetherComNcell;
    private Double meaDis;
    private String mml;
}
