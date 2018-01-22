package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmNcell;
import com.hgicreate.rno.service.gsm.dto.GsmNcellRelationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmNcellRelationMapper {

    GsmNcellRelationMapper INSTANCE = Mappers.getMapper( GsmNcellRelationMapper.class );

    /**
     * 邻区关系对象转换为DTO
     * @param gsmNcell 邻区关系对象
     * @return GsmNcellRelationDTO
     */
    @Mapping(source = "id", target = "id")
    GsmNcellRelationDTO ncellToNcellDTO(GsmNcell gsmNcell);
}
