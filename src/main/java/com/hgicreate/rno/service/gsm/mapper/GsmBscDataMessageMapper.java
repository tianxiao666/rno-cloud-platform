package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmBscData;
import com.hgicreate.rno.service.gsm.dto.GsmBscDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmBscDataMessageMapper {
    GsmBscDataMessageMapper INSTANCE = Mappers.getMapper(GsmBscDataMessageMapper.class);

    @Mapping(source = "area.name", target = "areaName")
    GsmBscDataDTO bscDataToBscDataDto(GsmBscData gsmBscData);
}
