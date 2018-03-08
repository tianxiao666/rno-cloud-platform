package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.CbPoi;
import com.hgicreate.rno.service.indoor.dto.CbPoiDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface CbPoiMapper {

    CbPoiMapper INSTANCE = Mappers.getMapper(CbPoiMapper.class);

    /**
     * cbPOI对象转换为DTO
     * @param cbPoi cbpoi对象
     * @return cbPOI DTO
     */
    @Mappings({
            @Mapping(source = "cbFloor.floorName",target = "floorName"),
            @Mapping(source = "poiId", target = "poiId")
    })
    CbPoiDTO cbPoiToCbPoiDTO(CbPoi cbPoi);
}
