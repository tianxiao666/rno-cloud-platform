package com.hgicreate.rno.service.gsm.dto;

import com.hgicreate.rno.util.FileSizeUtil;
import lombok.Data;

import java.util.Date;

@Data
public class GsmNcellImportFileDTO {
    private Long id;
    private Date uploadTime;
    private String areaName;
    private String filename;
    private String fileSize;
    private Date startTime;
    private Date completeTime;
    private String createdUser;
    private String status;

    public void setFileSize(String fileSize){
        this.fileSize = FileSizeUtil.getPrintSize(Long.parseLong(fileSize));
    }
}
