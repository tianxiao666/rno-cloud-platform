package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmNcsAnalysisDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GsmNcsAnalysisMapper {
    /**
     * NCS爱立信邻区分析查询
     */
    List<GsmNcsAnalysisDTO> queryGsmEriNcs(@Param("cityId")Long cityId, @Param("cell")String cell);
    /**
     * NCS华为邻区分析查询
     */
    List<GsmNcsAnalysisDTO> queryGsmHwNcs(@Param("cityId")Long cityId, @Param("cell")String cell);
    /**
     * 通过小区名英文名查找邻区
     */
    List<String> queryGsmNcell(@Param("cell")String cell);
}
