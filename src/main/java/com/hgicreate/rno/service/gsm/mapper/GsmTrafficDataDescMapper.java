package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmStsDescriptor;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficDataDescDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficDataDescVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmTrafficDataDescMapper {
    GsmTrafficDataDescMapper INSTANCE = Mappers.getMapper(GsmTrafficDataDescMapper.class);

    @Mappings({
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "createTime", target = "createTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "stsDate", target = "stsDate", dateFormat = "yyyy-MM-dd HH:mm:ss"),
    })
    GsmTrafficDataDescDTO trafficDataToTrafficDataDTO(GsmStsDescriptor gsmStsDescriptor);
}
