package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.service.dto.LteCellDataFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteCellDataFileMapper {

    LteCellDataFileMapper INSTANCE = Mappers.getMapper(LteCellDataFileMapper.class);

    @Mappings({
            @Mapping(source = "originFile.createdDate", target = "uploadTime"),
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "originFile.filename", target = "filename"),
            @Mapping(source = "originFile.fileType", target = "fileType"),
            @Mapping(source = "originFile.fileSize", target = "fileSize"),
            @Mapping(source = "originFile.fullPath", target = "fullPath"),
            @Mapping(source = "createdUser", target = "createdUser"),
            @Mapping(source = "status", target = "status")
    })
    LteCellDataFileDTO lteCellDataFileToLteCellDataFileDto(DataJob dataJob);
}
