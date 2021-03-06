package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficDataFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author yang.ch1
 */
@Mapper
public interface GsmTrafficDataFileMapper {

    GsmTrafficDataFileMapper INSTANCE = Mappers.getMapper(GsmTrafficDataFileMapper.class);

    /**
     * 将数据任务对象转换为话务文件DTO
     * @param dataJob 数据处理任务
     * @return 话务数据文件DTO
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "originFile.createdDate", target = "uploadTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "originFile.filename", target = "filename"),
            @Mapping(source = "originFile.fileSize", target = "fileSize"),
            @Mapping(source = "startTime", target = "startTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "completeTime", target = "completeTime",dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "createdUser", target = "createdUser"),
            @Mapping(source = "status", target = "status")
    })
    GsmTrafficDataFileDTO gsmTrafficFileToGsmTrafficFileDto(DataJob dataJob);
}
