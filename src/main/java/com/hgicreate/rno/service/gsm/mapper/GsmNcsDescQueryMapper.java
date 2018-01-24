package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.domain.gsm.GsmHwNcsDesc;
import com.hgicreate.rno.service.gsm.dto.GsmNcsAnalysisDescQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmNcsDescQueryMapper {
    GsmNcsDescQueryMapper INSTANCE = Mappers.getMapper(GsmNcsDescQueryMapper.class);

    /**
     * gsmHwNcsDesc 转换为 NCS邻区分析查询DTO
     * @param gsmHwNcsDesc gsmHwNcsDesc
     * @return NCS邻区分析查询DTO
     */
    @Mapping(source = "area.name", target = "areaName")
    GsmNcsAnalysisDescQueryDTO hwNcsDescQueryToDTO(GsmHwNcsDesc gsmHwNcsDesc);

    /**
     * gsmEriNcsDesc 转换为 NCS邻区分析查询DTO
     * @param gsmEriNcsDesc gsmEriNcsDesc
     * @return NCS邻区分析查询DTO
     */
    @Mapping(source = "area.name", target = "areaName")
    GsmNcsAnalysisDescQueryDTO eriNcsDescQueryToDTO(GsmEriNcsDesc gsmEriNcsDesc);
}
