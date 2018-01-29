package com.hgicreate.rno.service.indoor.dto;

import lombok.Data;

@Data
public class AreaDTO {

    private Long id;
    private String name;
    private Integer level;
    private Long parentId;
    private Double longitude;
    private Double latitude;
}
