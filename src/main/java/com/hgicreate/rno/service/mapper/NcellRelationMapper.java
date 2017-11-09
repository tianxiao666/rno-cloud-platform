package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.NcellRelation;
import com.hgicreate.rno.service.dto.NcellRelationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NcellRelationMapper {

    NcellRelationMapper INSTANCE = Mappers.getMapper( NcellRelationMapper.class );

    @Mapping(source = "id", target = "id")
    NcellRelationDTO ncellRelationToNcellRelationDTO(NcellRelation ncellRelation);
}
