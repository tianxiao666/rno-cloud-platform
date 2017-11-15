package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.service.dto.LteDtDataFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LteDtDataFileMapper {

    LteDtDataFileMapper INSTANCE = Mappers.getMapper(LteDtDataFileMapper.class);

    @Mappings({
            @Mapping(source = "originFile.createdDate", target = "uploadTime"),
            @Mapping(source = "area.name",target = "areaName"),
            @Mapping(source = "originFile.filename", target = "filename"),
            @Mapping(source = "originFile.fileSize", target = "fileSize")
    })
    LteDtDataFileDTO lteDtDataFileToLteDtDataFileDto(DataJob dataJob);
}
