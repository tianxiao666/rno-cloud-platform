package com.hgicreate.rno.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
public class GsmImportQueryDTO {
    private Long id;
    private String areaName;
    private Date createdDate;
    private String filename;
    private Integer fileSize;
    private Date startTime;
    private Date completeTime;
    private String createdUser;
    private String status;
}
