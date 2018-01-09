package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmDtSample;
import com.hgicreate.rno.service.gsm.dto.GsmDtAnalysisDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmDtAnalysisMapper {
    GsmDtAnalysisMapper INSTANCE = Mappers.getMapper(GsmDtAnalysisMapper.class);

    @Mapping(source = "id", target = "id")
    GsmDtAnalysisDTO gsmDtAnalysisToGsmDtAnalysisDTO(GsmDtSample gsmDtSample);
}
