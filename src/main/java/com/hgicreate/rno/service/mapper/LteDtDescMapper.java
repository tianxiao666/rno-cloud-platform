package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DtDesc;
import com.hgicreate.rno.service.dto.LteDtDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteDtDescMapper {

    LteDtDescMapper INSTANCE = Mappers.getMapper(LteDtDescMapper.class);


    @Mappings({
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "recordCount",target = "dataNum"),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    LteDtDescDTO lteDtDescToLteDtDescDTO(DtDesc lteDtDesc);

}
