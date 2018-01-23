package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmFasDesc;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.service.gsm.dto.GsmFasDataQueryDTO;
import com.hgicreate.rno.service.gsm.dto.GsmMrrDataQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author ke_weixu
 */
@Mapper
public interface GsmMrrDescMapper {
    GsmMrrDescMapper INSTANCE = Mappers.getMapper(GsmMrrDescMapper.class);

    @Mappings({
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "id", target = "id"),
    })
    GsmMrrDataQueryDTO gsmMrrDataQueryToDTO(GsmMrrDesc gsmMrrDesc);
}
