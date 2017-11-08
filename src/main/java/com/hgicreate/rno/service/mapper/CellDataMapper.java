package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.service.dto.CellDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Mapper
public interface CellDataMapper {

    CellDataMapper INSTANCE = Mappers.getMapper( CellDataMapper.class );

    @Mapping(source = "areaId", target = "areaName")
     CellDataDTO cellToCellData(Cell cell);
}
