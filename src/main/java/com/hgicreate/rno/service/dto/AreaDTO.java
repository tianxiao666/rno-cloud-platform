package com.hgicreate.rno.service.dto;

import lombok.Data;

@Data
public class AreaDTO {
    private Long id;

    private String name;
    private int level;
    private long parentId;
    private double longitude;
    private double latitude;
}
