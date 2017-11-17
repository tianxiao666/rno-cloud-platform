package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.service.dto.LteCellGisDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteCellGisMapper {
    LteCellGisMapper INSTANCE = Mappers.getMapper(LteCellGisMapper.class);

    LteCellGisDTO toLteCellGisDto(Cell cell);
}
