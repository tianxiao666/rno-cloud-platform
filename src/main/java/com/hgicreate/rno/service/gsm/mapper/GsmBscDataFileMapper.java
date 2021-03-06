package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.service.gsm.dto.GsmBscReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmBscDataFileMapper {

    GsmBscDataFileMapper INSTANCE = Mappers.getMapper(GsmBscDataFileMapper.class);

    /**
     * gsm网元导入记录对象转换为DTO
     * @param dataJob 数据任务对象
     * @return 数据任务对象DTO
     */
    @Mappings({
            @Mapping(source = "originFile.createdDate", target = "uploadTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "originFile.filename", target = "filename"),
            @Mapping(source = "originFile.fileSize", target = "fileSize"),
            @Mapping(source = "startTime", target = "startTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "completeTime", target = "completeTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "createdUser", target = "createdUser"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "id", target = "id")
    })
    GsmBscReportDTO bscDataFileToBscDataDTO(DataJob dataJob);
}
