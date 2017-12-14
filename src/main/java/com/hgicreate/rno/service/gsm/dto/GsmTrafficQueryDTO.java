package com.hgicreate.rno.service.gsm.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GsmTrafficQueryDTO {

    private Date stsDate;

    private String period;
    private String bsc;
    private String cellEnglishName;
    private String cellChineseName;
    private Float tRate;
    private Float defChannel;
    private Float availableChannel;
    private Float carrierNum;
    private Float wirelessUseRate;
    private Float trafficVolume;
    private Float preTrafficVolume;
}
