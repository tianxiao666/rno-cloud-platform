package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LteGridDataResultDTO {
    private String areaName;
    private String metaTime;
    private String gridSequ;
    private String gridDesc;
    private String gridCenter;
    private String createTime;
}
