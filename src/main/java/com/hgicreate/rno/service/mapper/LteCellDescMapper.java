package com.hgicreate.rno.service.mapper;


import com.hgicreate.rno.domain.LteCellDesc;
import com.hgicreate.rno.service.dto.LteCellDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteCellDescMapper {

    LteCellDescMapper INSTANCE = Mappers.getMapper(LteCellDescMapper.class);


    @Mappings({
         @Mapping(source = "area.name",target = "areaName"),
         @Mapping(source = "dataType", target = "dataType"),
         @Mapping(source = "filename", target = "filename"),
         @Mapping(source = "createdDate",target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss"),
         @Mapping(source = "recordCount", target = "dataNum")
    })
    LteCellDescDTO lteCellDescToLteCellDescDTO(LteCellDesc lteCellDesc);
}
