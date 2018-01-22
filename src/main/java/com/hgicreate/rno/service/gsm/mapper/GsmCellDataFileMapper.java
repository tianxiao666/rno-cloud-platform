package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.service.gsm.dto.GsmCellDataFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmCellDataFileMapper {

    GsmCellDataFileMapper INSTANCE = Mappers.getMapper(GsmCellDataFileMapper.class);

    /**
     * 数据任务对象转换为DTO
     * @param dataJob 数据任务对象
     * @return GsmCellDataFileDTO
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
    GsmCellDataFileDTO gsmCellDataFileToGsmCellDataFileDto(DataJob dataJob);
}
