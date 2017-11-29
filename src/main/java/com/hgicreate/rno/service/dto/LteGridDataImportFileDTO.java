package com.hgicreate.rno.service.dto;

import com.hgicreate.rno.util.FileSizeUtil;
import lombok.Data;

@Data
public class LteGridDataImportFileDTO {
    private Long id;
    private String uploadTime;
    private String areaName;
    private String filename;
    private String fileSize;
    private String startTime;
    private String completeTime;
    private String createdUser;
    private String status;

    public void setFileSize(String fileSize){
        this.fileSize = FileSizeUtil.getPrintSize(Long.parseLong(fileSize));
    }
}
