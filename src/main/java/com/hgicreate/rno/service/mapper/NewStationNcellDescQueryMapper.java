package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.NewStationDesc;
import com.hgicreate.rno.service.dto.NewStationNcellDescQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NewStationNcellDescQueryMapper {

    NewStationNcellDescQueryMapper INSTANCE = Mappers.getMapper( NewStationNcellDescQueryMapper.class );

    @Mapping(source = "area.name", target = "areaName")
    NewStationNcellDescQueryDTO gsmDescToDTO(NewStationDesc newStationDesc);
}
