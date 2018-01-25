package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.DmPlaneLayer;
import com.hgicreate.rno.service.indoor.dto.DmPlaneLayerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DmPlaneLayerMapper {

    DmPlaneLayerMapper INSTANCE = Mappers.getMapper(DmPlaneLayerMapper.class);

    DmPlaneLayerDTO dmPlaneLayerToDmPlaneLayerDTO(DmPlaneLayer dmPlaneLayer);
}
