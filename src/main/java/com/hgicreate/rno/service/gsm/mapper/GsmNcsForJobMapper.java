package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForJobDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmNcsForJobMapper {
    GsmNcsForJobMapper INSTANCE = Mappers.getMapper(GsmNcsForJobMapper.class);

    @Mappings({
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "meaTime", target = "meaDate",dateFormat = "yyyy-MM-dd HH:mm:ss"),
    })
    GsmNcsForJobDTO ncsForJobToNcsForJobDTO(GsmEriNcsDesc gsmEriNcsDesc);
}
