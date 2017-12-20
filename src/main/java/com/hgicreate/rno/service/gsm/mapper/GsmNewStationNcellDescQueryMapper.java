package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmLteNcellDesc;
import com.hgicreate.rno.domain.gsm.GsmNcellDesc;
import com.hgicreate.rno.service.gsm.dto.GsmNewStationNcellDescQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmNewStationNcellDescQueryMapper {

    GsmNewStationNcellDescQueryMapper INSTANCE = Mappers.getMapper( GsmNewStationNcellDescQueryMapper.class );

    @Mapping(source = "area", target = "area")
    @Mapping(source = "dataType", target = "dataType")
    @Mapping(source = "filename", target = "filename")
    @Mapping(source = "recordCount", target = "recordCount")
    @Mapping(source = "createdDate", target = "createdDate")
    GsmNewStationNcellDescQueryDTO gsmDescToDTO(GsmNcellDesc gsmNcellDesc);
    @Mapping(source = "area", target = "area")
    @Mapping(source = "dataType", target = "dataType")
    @Mapping(source = "filename", target = "filename")
    @Mapping(source = "recordCount", target = "recordCount")
    @Mapping(source = "createdDate", target = "createdDate")
    GsmNewStationNcellDescQueryDTO lteDescToDTO(GsmLteNcellDesc gsmLteNcellDesc);
}
