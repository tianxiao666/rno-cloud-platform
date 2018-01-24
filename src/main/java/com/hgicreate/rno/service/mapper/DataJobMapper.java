package com.hgicreate.rno.service.mapper;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.DataJobReport;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.dto.GsmImportQueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DataJobMapper {

    DataJobMapper INSTANCE = Mappers.getMapper(DataJobMapper.class);

    /**
     * DataJob 转换为 GSM查询导出记录DTO
     * @param dataJob dataJob
     * @return GSM查询导出记录DTO
     */
    @Mappings({
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "originFile.filename", target = "filename"),
            @Mapping(source = "originFile.fileSize", target = "fileSize"),
            @Mapping(source = "originFile.createdDate", target = "createdDate")
    })
    GsmImportQueryDTO gsmImportQueryToDTO(DataJob dataJob);
}
