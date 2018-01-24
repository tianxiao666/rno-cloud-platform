package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmDtDesc;
import com.hgicreate.rno.service.gsm.dto.GsmDtDescListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author ke_weixu
 */
@Mapper
public interface GsmDtDescMapper {
    GsmDtDescMapper INSTANCE = Mappers.getMapper(GsmDtDescMapper.class);

    /**
     * gsmDtDesc 转换为 路测数据信息DTO
     * @param gsmDtDesc
     * @return 路测数据信息DTO
     */
    GsmDtDescListDTO gsmDtDescListToDTO(GsmDtDesc gsmDtDesc);
}
