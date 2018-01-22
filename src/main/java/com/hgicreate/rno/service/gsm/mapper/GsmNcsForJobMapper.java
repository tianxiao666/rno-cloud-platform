package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmEriNcsDesc;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForJobDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author tao.xj
 */
@Mapper
public interface GsmNcsForJobMapper {
    GsmNcsForJobMapper INSTANCE = Mappers.getMapper(GsmNcsForJobMapper.class);

    /**
     * 将NCS数据描述对象转换为任务所需数据
     * @param gsmEriNcsDesc  NCS数据描述对象
     * @return GsmNcsForJobDTO
     */
    @Mappings({
            @Mapping(source = "area.name", target = "areaName"),
            @Mapping(source = "meaTime", target = "meaDate",dateFormat = "yyyy-MM-dd HH:mm:ss"),
    })
    GsmNcsForJobDTO ncsForJobToNcsForJobDTO(GsmEriNcsDesc gsmEriNcsDesc);
}
