package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmDtDesc;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.service.gsm.dto.GsmDtDescListDTO;
import com.hgicreate.rno.service.gsm.dto.GsmMrrDataQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author ke_weixu
 */
@Mapper
public interface GsmDtDescMapper {
    GsmDtDescMapper INSTANCE = Mappers.getMapper(GsmDtDescMapper.class);

    GsmDtDescListDTO gsmDtDescListToDTO(GsmDtDesc gsmDtDesc);
}
