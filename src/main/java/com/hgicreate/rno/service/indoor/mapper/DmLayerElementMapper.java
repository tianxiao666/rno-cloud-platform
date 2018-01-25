package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.DmLayerElement;
import com.hgicreate.rno.service.indoor.dto.DmLayerElementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DmLayerElementMapper {

    DmLayerElementMapper INSTANCE = Mappers.getMapper(DmLayerElementMapper.class);

    DmLayerElementDTO dmLayerElementToDmLayerElementDTO(DmLayerElement dmLayerElement);
}
