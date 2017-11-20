package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteMrDesc;
import com.hgicreate.rno.service.dto.LteMrDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteMrDescMapper {
    LteMrDescMapper INSTANCE = Mappers.getMapper(LteMrDescMapper.class);

    @Mappings({
            @Mapping(source = "area.name",target = "areaName"),
            @Mapping(source = "recordDate",target = "recordDate", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "createdDate",target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    LteMrDescDTO mrDescToMrDescDTO(LteMrDesc lteMrDesc);
}
