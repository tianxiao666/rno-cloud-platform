package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DataJobReport;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataJobReportMapper {

    DataJobReportMapper INSTANCE = Mappers.getMapper(DataJobReportMapper.class);

    @Mappings({
            @Mapping(source = "startTime", target = "startTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "completeTime", target = "completeTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })

    DataJobReportDTO dataJobReportToDataJobReportDTO(DataJobReport dataJobReport);
}
