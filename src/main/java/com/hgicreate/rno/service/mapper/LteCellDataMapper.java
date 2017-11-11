package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteCellDataMapper {
    LteCellDataMapper INSTANCE = Mappers.getMapper(LteCellDataMapper.class);

    @Mapping(source = "area.name", target = "areaName")
    LteCellDataDTO lteCellDataToLteCellDto(Cell cell);
}
