package com.hgicreate.rno.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
public class NewStationNcellDescQueryDTO {
    private String areaName;
    private String fileCode;
    private String filename;
    private Long recordCount;
    private Date createdDate;
}
