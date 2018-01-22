package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.DataJobReport;
import com.hgicreate.rno.domain.gsm.GsmStructJobReport;
import com.hgicreate.rno.service.dto.DataJobReportDTO;
import com.hgicreate.rno.service.gsm.dto.StructJobReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface StructJobReportMapper {

    StructJobReportMapper INSTANCE = Mappers.getMapper(StructJobReportMapper.class);

    /**
     * 将结构优化任务报告对象转换为DTO
     * @param gsmStructJobReport 结构优化任务报告对象
     * @return StructJobReportDTO
     */
    @Mappings({
            @Mapping(source = "startTime", target = "startTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "completeTime", target = "completeTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    })
    StructJobReportDTO structJobReportToStructJobReportDTO(GsmStructJobReport gsmStructJobReport);
}
