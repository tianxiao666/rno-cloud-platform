package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.service.dto.AreaDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AreaMapper {
    public AreaDTO areaToAreaDTO(Area area) {
        return new AreaDTO(area);
    }

    public List<AreaDTO> areasToAreaDTOs(List<Area> areas) {
        return areas.stream()
                .filter(Objects::nonNull)
                .map(this::areaToAreaDTO)
                .collect(Collectors.toList());
    }

    public Area areaDTOToArea(AreaDTO areaDTO) {
        if (areaDTO == null) {
            return null;
        } else {
            Area area = new Area();
            area.setId(areaDTO.getId());
            area.setName(areaDTO.getName());
            area.setAreaLevel(areaDTO.getLevel());
            area.setParentId(areaDTO.getParentId());
            area.setLongitude(areaDTO.getLongitude());
            area.setLatitude(areaDTO.getLatitude());
            return area;
        }
    }

    public List<Area> areaDTOsToAreas(List<AreaDTO> areaDTOs) {
        return areaDTOs.stream()
                .filter(Objects::nonNull)
                .map(this::areaDTOToArea)
                .collect(Collectors.toList());
    }
}
