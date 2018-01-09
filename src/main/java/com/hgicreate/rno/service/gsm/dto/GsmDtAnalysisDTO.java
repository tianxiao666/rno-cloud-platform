package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

/**
 * @author zeng.dh1
 */

@Data
public class GsmDtAnalysisDTO {
    private Long id;
    private String cell;
    private Double longitude ;
    private Double latitude ;
    private Integer rxlevsub ;
    private Integer rxqualsub ;
    private Integer ncellRxlevOne,ncellRxlevTwo,ncellRxlevThree,ncellRxlevFour,ncellRxlevFive,ncellRxlevSix;
}
