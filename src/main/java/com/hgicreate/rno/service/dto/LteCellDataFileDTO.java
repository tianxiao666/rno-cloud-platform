package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class LteCellDataFileDTO {

    private String uploadTime;
    private String areaName;
    private String filename;
    private String fileSize;
    private String startTime;
    private String completeTime;
    private String createdUser;
    private String status;

    public void setFileSize(String fileSize){
        this.fileSize = LteDtDataFileDTO.getPrintSize(Long.parseLong(fileSize));
    }

}
