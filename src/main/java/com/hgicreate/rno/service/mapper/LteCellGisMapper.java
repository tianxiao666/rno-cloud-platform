package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteCell;
import com.hgicreate.rno.service.dto.LteCellGisDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteCellGisMapper {
    LteCellGisMapper INSTANCE = Mappers.getMapper(LteCellGisMapper.class);

    LteCellGisDTO toLteCellGisDto(LteCell lteCell);
}
