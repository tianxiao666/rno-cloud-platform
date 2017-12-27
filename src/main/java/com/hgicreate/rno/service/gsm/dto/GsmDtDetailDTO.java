package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

@Data
public class GsmDtDetailDTO {
    private Long id;
    private String time ;
    private String cell ;
    private String rxlevsub ;
    private String rxqualsub ;
    private String ncellNameOne,ncellNameTwo,ncellNameThree,ncellNameFour,ncellNameFive,ncellNameSix;
    private String ncellAvgRxlev ;
    private String distance ;
    private String angle ;
}
