package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.service.dto.AreaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AreaMapper {

    AreaMapper INSTANCE = Mappers.getMapper( AreaMapper.class );

    @Mapping(source = "areaLevel", target = "level")
    AreaDTO areaToAreaDTO(Area area);
}
