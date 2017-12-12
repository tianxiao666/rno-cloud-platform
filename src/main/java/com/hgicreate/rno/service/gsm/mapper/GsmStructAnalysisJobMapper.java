package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmStructAnalysisJob;
import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmStructAnalysisJobMapper {
    GsmStructAnalysisJobMapper INSTANCE = Mappers.getMapper(GsmStructAnalysisJobMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "jobName"),
            @Mapping(source = "area.name",target = "cityName"),
            @Mapping(source = "begMeaTime", target = "begMeaTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "endMeaTime", target = "endMeaTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "startTime", target = "startTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "completeTime", target = "completeTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    GsmStructAnalysisJobDTO gsmStructAnalysisToGsmStructAnalysisDTO(GsmStructAnalysisJob gsmStructAnalysisJob);
}
