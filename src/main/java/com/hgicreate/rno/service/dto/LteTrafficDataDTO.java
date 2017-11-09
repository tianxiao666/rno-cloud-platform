package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LteTrafficDataDTO {
    private String cityName;
    private String uploadTime;
    private String fileName;
    private String fileSize;
    private String launchTime;
    private String completeTime;
    private String account;
    private String fileStatus;
}
