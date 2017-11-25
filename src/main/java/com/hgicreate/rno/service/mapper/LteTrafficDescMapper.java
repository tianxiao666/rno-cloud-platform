package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.LteTrafficDataDetail;
import com.hgicreate.rno.service.dto.LteTrafficDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteTrafficDescMapper {

    LteTrafficDescMapper INSTANCE = Mappers.getMapper(LteTrafficDescMapper.class);


    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "beginTime", target = "beginTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "endTime", target = "endTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    LteTrafficDescDTO lteTrafficDescToLteTrafficDescDTO(LteTrafficDataDetail lteTrafficDataDetail);
}
