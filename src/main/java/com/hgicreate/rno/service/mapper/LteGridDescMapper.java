package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteGridDesc;
import com.hgicreate.rno.service.dto.LteGridDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteGridDescMapper {
    LteGridDescMapper INSTANCE = Mappers.getMapper(LteGridDescMapper.class);

    @Mappings({
            @Mapping(source = "area.name",target = "areaName"),
            @Mapping(source = "createdDate",target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    LteGridDescDTO gridDescToGridDescDTO(LteGridDesc lteGridDesc);
}
