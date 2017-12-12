package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamChangeVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GsmParamChangeMapper {
    List<Map<String, Object>> eriCellParamsCompare(GsmParamChangeVM vm);
    List<Map<String, Object>> eriChannelParamsCompare(GsmParamChangeVM vm);
    List<Map<String, Object>> eriNeighbourParamsCompare(GsmParamChangeVM vm);

    Integer typeCellDataNumberOnTheDate(GsmParamChangeVM vm);
    Integer typeChannelDataNumberOnTheDate(GsmParamChangeVM vm);
    Integer typeNeighbourDataNumberOnTheDate(GsmParamChangeVM vm);

    List<LinkedHashMap<String, Object>> getBscById(GsmParamChangeVM vm);

    List<Map<String, Object>> eriCellParamsDetail(GsmParamChangeVM vm);
    List<Map<String, Object>> eriChannelParamsDetail(GsmParamChangeVM vm);
    List<Map<String, Object>> eriNeighbourParamsDetail(GsmParamChangeVM vm);

    List<Map<String, Object>> eriCellParamsCompareResult(GsmParamChangeVM vm);
    List<Map<String, Object>> eriChannelParamsCompareResult(GsmParamChangeVM vm);
    List<Map<String, Object>> eriNeighbourParamsCompareResult(GsmParamChangeVM vm);

}
