package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.CbPoi;
import com.hgicreate.rno.service.indoor.dto.CbPoiDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CbPoiMapper {

    CbPoiMapper INSTANCE = Mappers.getMapper(CbPoiMapper.class);

    @Mappings({
            @Mapping(source = "cbFloor.floorName",target = "floorName"),
            @Mapping(source = "poiId", target = "poiId")
    })
    CbPoiDTO cbPoiToCbPoiDTO(CbPoi cbPoi);
}
