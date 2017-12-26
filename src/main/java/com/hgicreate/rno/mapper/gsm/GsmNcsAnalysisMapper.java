package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmNcsAnalysisDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GsmNcsAnalysisMapper {
    List<GsmNcsAnalysisDTO> queryGsmEriNcs(@Param("cityId")Long cityId, @Param("cell")String cell);
    List<GsmNcsAnalysisDTO> queryGsmHwNcs(@Param("cityId")Long cityId, @Param("cell")String cell);
    List<String> queryGsmNcell(@Param("cell")String cell);
}
