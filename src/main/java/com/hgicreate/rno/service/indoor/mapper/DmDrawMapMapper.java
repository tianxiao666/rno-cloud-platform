package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.DmDrawMap;
import com.hgicreate.rno.service.indoor.dto.DmDrawMapDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DmDrawMapMapper {

    DmDrawMapMapper INSTANCE = Mappers.getMapper(DmDrawMapMapper.class);

    @Mappings({
            @Mapping(source = "cbFloor.floorName",target = "floorName"),
            @Mapping(source = "drawMapId", target = "drawMapId"),
    })
    DmDrawMapDTO dmDrawMapToDmDrawMapDTO(DmDrawMap dmDrawMap);
}
