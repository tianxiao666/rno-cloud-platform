package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;
/**
 * @author ke_weixu
 */
@Data
public class GsmFasDataQueryDTO {
    private String areaName;
    private Date meaTime;
    private String bsc;
    private Long recordNum;
    private Date createTime;
}
