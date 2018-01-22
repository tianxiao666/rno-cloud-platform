package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.service.dto.LteNcellImportFileDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNcellImportFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmNcellImportFileMapper {

    GsmNcellImportFileMapper INSTANCE = Mappers.getMapper(GsmNcellImportFileMapper.class);

    /**
     * 数据任务对象转换为DTO
     * @param dataJob 数据任务对象
     * @return GsmNcellImportFileDTO
     */
    @Mappings({
            @Mapping(source = "originFile.createdDate", target = "uploadTime"),
            @Mapping(source = "area.name",target = "areaName"),
            @Mapping(source = "originFile.filename", target = "filename"),
            @Mapping(source = "originFile.fileSize", target = "fileSize"),
            @Mapping(source = "id", target = "id")
    })
    GsmNcellImportFileDTO ncellImportFileToNcellImportFileDTO(DataJob dataJob);
}
