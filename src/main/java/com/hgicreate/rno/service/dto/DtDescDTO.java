package com.hgicreate.rno.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtDescDTO {

    private Long id;
    private String dataType;
    private String areaType;
    private String filename;

}
