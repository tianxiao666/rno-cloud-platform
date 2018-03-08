package com.hgicreate.rno.service.indoor.mapper;

import com.hgicreate.rno.domain.indoor.DmLayerElement;
import com.hgicreate.rno.service.indoor.dto.DmLayerElementDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author chao.xj
 */
@Mapper
public interface DmLayerElementMapper {

    DmLayerElementMapper INSTANCE = Mappers.getMapper(DmLayerElementMapper.class);

    /**
     * 图层元素转换为DTO
     * @param dmLayerElement 图层元素对象
     * @return 图层元素DTO
     */
    DmLayerElementDTO dmLayerElementToDmLayerElementDTO(DmLayerElement dmLayerElement);
}
