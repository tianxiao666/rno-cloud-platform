package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.IdealApMeaData;
import com.hgicreate.rno.domain.indoor.IdealApMeaDataInfo;
import com.hgicreate.rno.service.indoor.dto.IdealApMeaDataDTO;
import com.hgicreate.rno.service.indoor.dto.IdealApMeaDataInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IdealApMeaDataMapper {

    IdealApMeaDataMapper INSTANCE = Mappers.getMapper(IdealApMeaDataMapper.class);

    @Mapping(source = "id", target = "id")
    IdealApMeaDataInfoDTO idealApMeaDataInfoToIdealApMeaDataInfoDTO(IdealApMeaDataInfo idealApMeaDataInfo);

    @Mapping(source = "idealApId", target = "idealApId")
    IdealApMeaDataDTO idealApMeaDataToIdealApMeaDataDTO(IdealApMeaData idealApMeaData);
}
