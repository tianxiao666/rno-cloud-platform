package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.MtSignalMeaData;
import com.hgicreate.rno.domain.indoor.MtSignalMeaDataInfo;
import com.hgicreate.rno.service.indoor.dto.MtSignalMeaDataDTO;
import com.hgicreate.rno.service.indoor.dto.MtSignalMeaDataInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MtSignalMeaDataMapper {

    MtSignalMeaDataMapper INSTANCE = Mappers.getMapper(MtSignalMeaDataMapper.class);

    @Mapping(source = "id", target = "id")
    MtSignalMeaDataInfoDTO mtSignalMeaDataInfoToMtSignalMeaDataInfoDTO(MtSignalMeaDataInfo mtSignalMeaDataInfo);

    @Mapping(source = "signalId", target = "signalId")
    MtSignalMeaDataDTO mtSignalMeaDataToMtSignalMeaDataDTO(MtSignalMeaData mtSignalMeaData);

}
