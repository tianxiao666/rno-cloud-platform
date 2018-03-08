package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.ApEquipment;
import com.hgicreate.rno.service.indoor.dto.ApEquipmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface ApEquipmentMapper {

    ApEquipmentMapper INSTANCE = Mappers.getMapper(ApEquipmentMapper.class);

    /**
     * ap设备对象转换为DTO
     * @param apEquipment 定位设备对象
     * @return 定位设备DTO
     */
    @Mappings({
            @Mapping(source = "cbFloor.floorName",target = "floorName"),
            @Mapping(source = "apId", target = "apId")
    })
    ApEquipmentDTO apEquipmentToApEquipmentDTO(ApEquipment apEquipment);
}
