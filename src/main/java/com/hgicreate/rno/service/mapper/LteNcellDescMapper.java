package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteNcellDesc;
import com.hgicreate.rno.service.dto.LteNcellDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteNcellDescMapper {
    LteNcellDescMapper INSTANCE = Mappers.getMapper(LteNcellDescMapper.class);

    @Mappings({
            @Mapping(source = "area.name",target = "areaName"),
            @Mapping(source = "createdDate",target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    LteNcellDescDTO ncellDescToNcellDescDTO(LteNcellDesc lteNcellDesc);
}
