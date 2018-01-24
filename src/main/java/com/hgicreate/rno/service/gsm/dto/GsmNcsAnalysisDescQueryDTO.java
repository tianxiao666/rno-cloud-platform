package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ke_weixu
 */
@Data
public class GsmNcsAnalysisDescQueryDTO {
    private String areaName;
    private Date meaTime;
    private String bsc;
    private Long recordCount;
    private Date createTime;


}
