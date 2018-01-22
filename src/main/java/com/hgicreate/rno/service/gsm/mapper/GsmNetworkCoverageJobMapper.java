package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmNetworkCoverageJob;
import com.hgicreate.rno.service.gsm.dto.GsmNetworkCoverageJobDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmNetworkCoverageJobMapper {
    GsmNetworkCoverageJobMapper INSTANCE = Mappers.getMapper(GsmNetworkCoverageJobMapper.class);

    /**
     * 将网络覆盖任务对象转换为DTO
     * @param gsmNetworkCoverageJob 网络覆盖任务对象
     * @return GsmNetworkCoverageJobDTO
     */
    @Mappings({
            @Mapping(source = "id",target = "id"),
            @Mapping(source = "createdDate", target = "createdDate",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "area.name", target = "cityName"),
            @Mapping(source = "begMeaTime", target = "begMeaTime",dateFormat = "yyyy-MM-dd"),
            @Mapping(source = "endMeaTime", target = "endMeaTime",dateFormat = "yyyy-MM-dd")
    })
    GsmNetworkCoverageJobDTO gsmNetworkCoverageToGsmNetworkCoverageDTO(GsmNetworkCoverageJob gsmNetworkCoverageJob);
}
