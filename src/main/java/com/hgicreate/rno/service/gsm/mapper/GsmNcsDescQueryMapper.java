package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.domain.gsm.GsmHwNcsDesc;
import com.hgicreate.rno.service.gsm.dto.GsmNcsDescQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmNcsDescQueryMapper {
    GsmNcsDescQueryMapper INSTANCE = Mappers.getMapper(GsmNcsDescQueryMapper.class);

    GsmNcsDescQueryDTO hwNcsDescQueryToDTO(GsmHwNcsDesc gsmHwNcsDesc);

    GsmNcsDescQueryDTO eriNcsDescQueryToDTO(GsmEriNcsDesc gsmEriNcsDesc);
}
