package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmBscData;
import com.hgicreate.rno.service.gsm.dto.GsmBscDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmBscDataMessageMapper {
    GsmBscDataMessageMapper INSTANCE = Mappers.getMapper(GsmBscDataMessageMapper.class);
    /**
     * gsm网元数据对象转换为DTO
     * @param gsmBscData bsc数据对象
     * @return bsc数据对象DTO
     */
    @Mapping(source = "area.name", target = "areaName")
    GsmBscDataDTO bscDataToBscDataDto(GsmBscData gsmBscData);
}
