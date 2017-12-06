package com.hgicreate.rno.service.gsm.mapper;


import com.hgicreate.rno.domain.LteCellDesc;
import com.hgicreate.rno.domain.gsm.GsmCellDesc;
import com.hgicreate.rno.service.dto.LteCellDescDTO;
import com.hgicreate.rno.service.gsm.dto.GsmCellDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmCellDescMapper {

    GsmCellDescMapper INSTANCE = Mappers.getMapper(GsmCellDescMapper.class);


    @Mappings({
         @Mapping(source = "area.name",target = "areaName"),
         @Mapping(source = "dataType", target = "dataType"),
         @Mapping(source = "filename", target = "filename"),
         @Mapping(source = "createdDate",target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss"),
         @Mapping(source = "recordCount", target = "dataNum")
    })
    GsmCellDescDTO gsmCellDescToGsmCellDescDTO(GsmCellDesc gsmCellDesc);
}
