package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamChangeVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GsmParamChangeMapper {
    //参数比较
    List<Map<String, Object>> eriCellParamsCompare(GsmParamChangeVM vm);
    List<Map<String, Object>> eriChannelParamsCompare(GsmParamChangeVM vm);
    List<Map<String, Object>> eriNeighbourParamsCompare(GsmParamChangeVM vm);
    //匹配日期数据数量
    Integer typeCellDataNumberOnTheDate(GsmParamChangeVM vm);
    Integer typeChannelDataNumberOnTheDate(GsmParamChangeVM vm);
    Integer typeNeighbourDataNumberOnTheDate(GsmParamChangeVM vm);

    List<LinkedHashMap<String, Object>> getBscById(GsmParamChangeVM vm);

    //参数差异详情
    List<Map<String, Object>> eriCellParamsDetail(GsmParamChangeVM vm);
    List<Map<String, Object>> eriChannelParamsDetail(GsmParamChangeVM vm);
    List<Map<String, Object>> eriNeighbourParamsDetail(GsmParamChangeVM vm);
    //参数差异导出
    List<Map<String, Object>> eriCellParamsCompareResult(GsmParamChangeVM vm);
    List<Map<String, Object>> eriChannelParamsCompareResult(GsmParamChangeVM vm);
    List<Map<String, Object>> eriNeighbourParamsCompareResult(GsmParamChangeVM vm);

}
