package com.hgicreate.rno.service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LteNcellImportFileDTO {
    private Long id;
    private String uploadTime;
    private String areaName;
    private String filename;
    private String fileSize;
    private String startTime;
    private String completeTime;
    private String createdUser;
    private String status;
}
