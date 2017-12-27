package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmDtSample;
import com.hgicreate.rno.service.gsm.dto.GsmDtDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmDtDetailMapper {
    GsmDtDetailMapper INSTANCE = Mappers.getMapper(GsmDtDetailMapper.class);

    @Mapping(source = "sampleTime", target = "time", dateFormat = "yyyy-MM-dd HH:mm:ss")

    GsmDtDetailDTO gsmDtDetailToGsmDtDetailDTO(GsmDtSample gsmDtSample);
}
