package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.service.dto.AreaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface AreaMapper {

    com.hgicreate.rno.service.mapper.AreaMapper INSTANCE = Mappers.getMapper( com.hgicreate.rno.service.mapper.AreaMapper.class );

    /**
     *区域对象转换为DTO
     * @param area 区域对象
     * @return 区域DTO
     */
    @Mapping(source = "areaLevel", target = "level")
    AreaDTO areaToAreaDTO(Area area);
}
