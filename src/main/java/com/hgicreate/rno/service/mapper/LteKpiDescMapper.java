package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteKpiDesc;
import com.hgicreate.rno.service.dto.LteKpiDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteKpiDescMapper {
    LteKpiDescMapper INSTANCE = Mappers.getMapper(LteKpiDescMapper.class);


    @Mappings({
            @Mapping(source = "area.name",target = "areaName"),
            @Mapping(source = "dataType", target = "dataType"),
            @Mapping(source = "filename", target = "filename"),
            @Mapping(source = "createdDate",target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "recordCount", target = "dataNum")
    })
    LteKpiDescDTO lteKpiDescToLteKpiDescDTO(LteKpiDesc lteKpiDesc);
}
