package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.MtSignalMeaData;
import com.hgicreate.rno.domain.indoor.MtSignalMeaDataInfo;
import com.hgicreate.rno.service.indoor.dto.MtSignalMeaDataDTO;
import com.hgicreate.rno.service.indoor.dto.MtSignalMeaDataInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface MtSignalMeaDataMapper {

    MtSignalMeaDataMapper INSTANCE = Mappers.getMapper(MtSignalMeaDataMapper.class);

    /**
     * 移动信号测量数据对象转换为DTO
     * @param mtSignalMeaDataInfo 移动信号测量数据对象
     * @return 移动信号测量数据对象DTO
     */
    @Mapping(source = "id", target = "id")
    MtSignalMeaDataInfoDTO mtSignalMeaDataInfoToMtSignalMeaDataInfoDTO(MtSignalMeaDataInfo mtSignalMeaDataInfo);

}
