package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GsmDtAnalysisDTO {
    private Long id;
    private Double longitute ;
    private Double latitude ;
    private Integer rxlevsub ;
    private Integer rxqualsub ;
}
