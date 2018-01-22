package com.hgicreate.rno.service.gsm.dto;

import com.hgicreate.rno.util.FileSizeUtil;
import lombok.Data;

/**
 * @author yang.ch1
 */
@Data
public class GsmTrafficDataFileDTO {

    private String id;
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