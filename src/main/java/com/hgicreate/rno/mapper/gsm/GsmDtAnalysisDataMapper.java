package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamCheckVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmDtAnalysisDataMapper {
    List<Map<String, Object>> getDistinctCell();
    List<Map<String, Object>> getCoverageCell(String cellId);
    List<Map<String, Object>> getCoverageSample(String cellId);
    List<Map<String, Object>> getNcell(Double longitude,Double latitude);
}
