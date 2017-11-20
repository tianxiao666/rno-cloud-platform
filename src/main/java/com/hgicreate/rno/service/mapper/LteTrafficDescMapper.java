package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DtDesc;
import com.hgicreate.rno.domain.TrafficDesc;
import com.hgicreate.rno.service.dto.LteDtDescDTO;
import com.hgicreate.rno.service.dto.LteTrafficDescDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteTrafficDescMapper {

    LteTrafficDescMapper INSTANCE = Mappers.getMapper(LteTrafficDescMapper.class);


    @Mappings({
            @Mapping(source = "area.name", target = "areaName"),
            /*@Mapping(source = "trafficData.pmDn", target = "pmDn"),*/
            @Mapping(source = "beginTime", target = "beginTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "endTime", target = "endTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "createdDate", target = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    LteTrafficDescDTO lteTrafficDescToLteTrafficDescDTO(TrafficDesc trafficDesc);

}
