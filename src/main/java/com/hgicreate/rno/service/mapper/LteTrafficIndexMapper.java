package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteTrafficIndex;
import com.hgicreate.rno.service.dto.LteTrafficIndexDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteTrafficIndexMapper {
    LteTrafficIndexMapper INSTANCE = Mappers.getMapper(LteTrafficIndexMapper.class);

    LteTrafficIndexDTO trafficIndexToTrafficIndexDTO(LteTrafficIndex lteTrafficIndex);
}
