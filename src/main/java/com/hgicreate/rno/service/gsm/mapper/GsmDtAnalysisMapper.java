package com.hgicreate.rno.service.gsm.mapper;

import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.domain.gsm.GsmDtData;
import com.hgicreate.rno.service.gsm.dto.GsmCellDataDTO;
import com.hgicreate.rno.service.gsm.dto.GsmDtAnalysisDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GsmDtAnalysisMapper {
    GsmDtAnalysisMapper INSTANCE = Mappers.getMapper(GsmDtAnalysisMapper.class);

    @Mappings({
        @Mapping(source = "gsmDtData.rxlevsub", target = "rxlevsub"),
        @Mapping(source = "gsmDtData.rxqualsub", target = "rxqualsub"),
    })
    GsmDtAnalysisDTO gsmDtAnalysisToGsmDtAnalysisDTO(GsmDtData gsmDtData);
}
