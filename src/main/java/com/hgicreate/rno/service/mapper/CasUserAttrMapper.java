package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.User;
import com.hgicreate.rno.service.dto.AreaDTO;
import com.hgicreate.rno.service.dto.CasUserAttrDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CasUserAttrMapper {

    CasUserAttrMapper INSTANCE = Mappers.getMapper( CasUserAttrMapper.class );

    @Mapping(source = "phoneNumber", target = "phone")
    CasUserAttrDTO areaToAreaDTO(User user);
}
