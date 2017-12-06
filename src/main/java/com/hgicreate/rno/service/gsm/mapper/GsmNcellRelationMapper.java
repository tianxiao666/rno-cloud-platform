package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.LteNcellRelation;
import com.hgicreate.rno.domain.gsm.GsmNcell;
import com.hgicreate.rno.domain.gsm.GsmNcellRelation;
import com.hgicreate.rno.service.dto.LteNcellRelationDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcellRelationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmNcellRelationMapper {

    GsmNcellRelationMapper INSTANCE = Mappers.getMapper( GsmNcellRelationMapper.class );

    @Mapping(source = "id", target = "id")
    GsmNcellRelationDTO ncellToNcellDTO(GsmNcell gsmNcell);
}
