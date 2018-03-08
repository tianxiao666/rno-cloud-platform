package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.IdealApMeaData;
import com.hgicreate.rno.domain.indoor.IdealApMeaDataInfo;
import com.hgicreate.rno.service.indoor.dto.IdealApMeaDataDTO;
import com.hgicreate.rno.service.indoor.dto.IdealApMeaDataInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface IdealApMeaDataMapper {

    IdealApMeaDataMapper INSTANCE = Mappers.getMapper(IdealApMeaDataMapper.class);

    /**
     * AP采集数据对象转换为DTO
     * @param idealApMeaDataInfo AP采集数据对象
     * @return AP采集数据DTO
     */
    @Mapping(source = "id", target = "id")
    IdealApMeaDataInfoDTO idealApMeaDataInfoToIdealApMeaDataInfoDTO(IdealApMeaDataInfo idealApMeaDataInfo);

}
