package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.CbFloor;
import com.hgicreate.rno.service.indoor.dto.CbFloorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface CbFloorMapper {

    CbFloorMapper INSTANCE = Mappers.getMapper(CbFloorMapper.class);

    /**
     * 楼层对象转换为DTO
     * @param cbFloor 楼层对象
     * @return 楼层DTO
     */
    @Mapping(source = "floorId", target = "floorId")
    CbFloorDTO cbFloorToCbFloorDTO(CbFloor cbFloor);
}
