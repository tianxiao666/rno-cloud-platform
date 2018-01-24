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

    @Mapping(source = "area.name", target = "areaName")
    GsmNcsAnalysisDescQueryDTO hwNcsDescQueryToDTO(GsmHwNcsDesc gsmHwNcsDesc);
    @Mapping(source = "area.name", target = "areaName")
    GsmNcsAnalysisDescQueryDTO eriNcsDescQueryToDTO(GsmEriNcsDesc gsmEriNcsDesc);
}
