package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class LteCellDataFileDTO {

    private String uploadTime;
    private String areaName;
    private String filename;
    private String fileType;
    private String fileSize;
    private String fullPath;
    private String createdUser;
    private String status;
}
