package com.hgicreate.rno.service.gsm.dto;

import com.hgicreate.rno.util.FileSizeUtil;
import lombok.Data;

/**
 * @author tao.xj
 */
@Data
public class GsmCellDataFileDTO {

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
