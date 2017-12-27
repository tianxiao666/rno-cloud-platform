package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GsmNcsAnalysisDTO {
    private String cell;
    private Long bsic;
    private Long arfcn;
    private Long rep;
    private Float topsix;
    private Float toptwo;
    private Float abss;
    private Float alone;
    private String ncell;
    private Float distance;
    private String ncells;
    private Long defined;
    private Date meaTime;
    private Float cellRate;
    private Long ncsId;
    private String manufacturers;
}
