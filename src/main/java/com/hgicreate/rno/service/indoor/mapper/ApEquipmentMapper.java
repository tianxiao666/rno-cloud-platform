package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.ApEquipment;
import com.hgicreate.rno.service.indoor.dto.ApEquipmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApEquipmentMapper {

    ApEquipmentMapper INSTANCE = Mappers.getMapper(ApEquipmentMapper.class);

    @Mappings({
            @Mapping(source = "cbFloor.floorName",target = "floorName"),
            @Mapping(source = "apId", target = "apId")
    })
    ApEquipmentDTO apEquipmentToApEquipmentDTO(ApEquipment apEquipment);
}
