package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DtData;
import com.hgicreate.rno.service.dto.DtDataDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DtDataMapper {
    DtDataMapper INSTANCE = Mappers.getMapper(DtDataMapper.class);

    DtDataDTO toDtDataDto(DtData dtData);
}
