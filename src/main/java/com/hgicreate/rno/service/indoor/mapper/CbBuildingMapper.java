package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.CbBuilding;
import com.hgicreate.rno.service.indoor.dto.CbBuildingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface CbBuildingMapper {

    CbBuildingMapper INSTANCE = Mappers.getMapper(CbBuildingMapper.class);

    /**
     * 场所对象转换为DTO
     * @param cbBuilding 场所对象
     * @return 场所对象DTO
     */
    @Mappings({
            @Mapping(source = "area1.name",target = "prov"),
            @Mapping(source = "area.name", target = "city"),
            @Mapping(source = "buildingId", target = "id")
    })
    CbBuildingDTO cbBuildingTocbBuildingDTO(CbBuilding cbBuilding);
}
