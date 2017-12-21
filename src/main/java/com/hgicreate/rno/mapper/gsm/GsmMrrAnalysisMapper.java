package com.hgicreate.rno.mapper.gsm;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmMrrAnalysisMapper {
    List<Map<String, Object>> queryAllBscByAreaId(long areaId);
}
