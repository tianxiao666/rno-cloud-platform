package com.hgicreate.rno.service.gsm.dto;

import com.hgicreate.rno.domain.Area;
import lombok.Data;

import java.util.Date;

@Data
public class GsmNewStationNcellDescQueryDTO {
    private Area area;
    private String dataType;
    private String filename;
    private Long recordCount;
    private Date createdDate;
}
