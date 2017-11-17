package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DtDesc;
import com.hgicreate.rno.service.dto.DtDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DtDescMapper {
    DtDescMapper INSTANCE = Mappers.getMapper(DtDescMapper.class);

    DtDescDTO toDtDescDto(DtDesc dtDesc);
}
