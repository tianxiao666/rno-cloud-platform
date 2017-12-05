package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.BscData;
import com.hgicreate.rno.service.gsm.dto.BscDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BscDataMessageMapper {
    BscDataMessageMapper INSTANCE = Mappers.getMapper(BscDataMessageMapper.class);

    @Mapping(source = "area.name", target = "areaName")
    BscDataDTO bscDataToBscDataDto(BscData bscData);
}
