package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteNcellRelation;
import com.hgicreate.rno.service.dto.LteNcellRelationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteNcellRelationMapper {

    LteNcellRelationMapper INSTANCE = Mappers.getMapper( LteNcellRelationMapper.class );

    @Mapping(source = "id", target = "id")
    LteNcellRelationDTO ncellRelationToNcellRelationDTO(LteNcellRelation lteNcellRelation);
}
