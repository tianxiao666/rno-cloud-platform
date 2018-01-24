package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmFasDesc;
import com.hgicreate.rno.service.gsm.dto.GsmFasDataQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author ke_weixu
 */
@Mapper
public interface GsmFasDescMapper {
    GsmFasDescMapper INSTANCE = Mappers.getMapper(GsmFasDescMapper.class);

    /**
     * gsmFasDesc 转换为 Fas数据查询DTO
     * @param gsmFasDesc gsmFasDesc
     * @return Fas数据查询DTO
     */
    @Mapping(source = "area.name", target = "areaName")
    GsmFasDataQueryDTO gsmFasDataQueryToDTO(GsmFasDesc gsmFasDesc);
}
