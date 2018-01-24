package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.service.gsm.dto.GsmNcsDescQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author ke_weixu
 */
@Mapper
public interface GsmEriNcsDescMapper {
    GsmEriNcsDescMapper INSTANCE = Mappers.getMapper(GsmEriNcsDescMapper.class);

    GsmNcsDescQueryDTO gsmNcsDescQueryToDTO(GsmEriNcsDesc gsmEriNcsDesc);
}
