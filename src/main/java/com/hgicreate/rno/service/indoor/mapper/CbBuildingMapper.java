package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.CbBuilding;
import com.hgicreate.rno.service.indoor.dto.CbBuildingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CbBuildingMapper {

    CbBuildingMapper INSTANCE = Mappers.getMapper(CbBuildingMapper.class);

    @Mappings({
            @Mapping(source = "area1.name",target = "prov"),
            @Mapping(source = "area.name", target = "city"),
            @Mapping(source = "buildingId", target = "id")
    })
    CbBuildingDTO cbBuildingTocbBuildingDTO(CbBuilding cbBuilding);
}
