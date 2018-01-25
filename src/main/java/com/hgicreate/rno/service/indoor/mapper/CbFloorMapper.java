package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.CbFloor;
import com.hgicreate.rno.service.indoor.dto.CbFloorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CbFloorMapper {

    CbFloorMapper INSTANCE = Mappers.getMapper(CbFloorMapper.class);

    @Mapping(source = "floorId", target = "floorId")
    CbFloorDTO cbFloorToCbFloorDTO(CbFloor cbFloor);
}
