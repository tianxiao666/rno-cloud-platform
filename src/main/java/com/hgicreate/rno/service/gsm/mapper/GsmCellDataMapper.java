package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.service.gsm.dto.GsmCellDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmCellDataMapper {
    GsmCellDataMapper INSTANCE = Mappers.getMapper(GsmCellDataMapper.class);

    /**
     * 小区对象转换为DTO
     * @param gsmCell 小区对象
     * @return GsmCellDataDTO
     */
    @Mapping(source = "area.name", target = "areaName")
    GsmCellDataDTO gsmCellDataToGsmCellDto(GsmCell gsmCell);
}
