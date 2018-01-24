package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.NewStationDesc;
import com.hgicreate.rno.service.dto.NewStationNcellDescQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author ke_weixu
 */
@Mapper
public interface NewStationNcellDescQueryMapper {

    NewStationNcellDescQueryMapper INSTANCE = Mappers.getMapper( NewStationNcellDescQueryMapper.class );

    /**
     * newStationDesc 转换为 新站邻区描述查询DTO
     * @param newStationDesc newStationDesc
     * @return 新站邻区描述查询DTO
     */
    @Mapping(source = "area.name", target = "areaName")
    NewStationNcellDescQueryDTO gsmDescToDTO(NewStationDesc newStationDesc);
}
