package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GsmNcsDescQueryDTO {
    private String areaName;
    private Date meaTime;
    private String bsc;
    private Long recordCount;
    private Date createTime;

}
