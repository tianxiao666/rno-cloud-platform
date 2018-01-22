package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmDtSample;
import com.hgicreate.rno.service.gsm.dto.GsmDtDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmDtDetailMapper {
    GsmDtDetailMapper INSTANCE = Mappers.getMapper(GsmDtDetailMapper.class);
    /**
     * gsm路测采样点详情对象转换为DTO
     * @param gsmDtSample 路测采样点详情数据描述对象
     * @return 路测采样点详情数据描述DTO
     */
    @Mapping(source = "sampleTime", target = "time", dateFormat = "yyyy-MM-dd HH:mm:ss")
    GsmDtDetailDTO gsmDtDetailToGsmDtDetailDTO(GsmDtSample gsmDtSample);
}
