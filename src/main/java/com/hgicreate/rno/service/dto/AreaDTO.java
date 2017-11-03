package com.hgicreate.rno.service.dto;

import com.hgicreate.rno.domain.Area;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaDTO {
    private Long id;

    private String name;
    private int level;
    private long parentId;
    private double longitude;
    private double latitude;

    public AreaDTO(Area area) {
        this(area.getId(), area.getName(), area.getAreaLevel(), area.getParentId(),
                area.getLongitude(), area.getLatitude());
    }
}
