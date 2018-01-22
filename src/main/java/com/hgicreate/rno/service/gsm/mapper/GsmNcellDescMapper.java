package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmNcellDesc;
import com.hgicreate.rno.service.gsm.dto.GsmNcellDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmNcellDescMapper {
    GsmNcellDescMapper INSTANCE = Mappers.getMapper(GsmNcellDescMapper.class);

    /**
     * 邻区数据描述对象转换为DTO
     * @param gsmNcellDesc 邻区数据描述对象
     * @return GsmNcellDescDTO
     */
    @Mappings({
            @Mapping(source = "area.name",target = "areaName"),
            @Mapping(source = "createdDate",target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    GsmNcellDescDTO ncellDescToNcellDescDTO(GsmNcellDesc gsmNcellDesc);
}
