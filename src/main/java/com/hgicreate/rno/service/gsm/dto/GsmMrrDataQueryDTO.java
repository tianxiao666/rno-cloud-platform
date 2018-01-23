package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
public class GsmMrrDataQueryDTO {
    private Long id;
    private String areaName;
    private Date meaDate;
    private String bsc;
    private String fileName;
}
