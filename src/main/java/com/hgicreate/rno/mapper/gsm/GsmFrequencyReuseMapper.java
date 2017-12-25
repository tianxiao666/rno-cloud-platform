package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmGisCellVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmFrequencyReuseMapper {

    List<GsmGisCellVM> selectFreqReuseCellGisInfoFromSelectionList(Map<String, Object> map);
    List<Map<String, Object>> selectBcchTchFrom(Map<String, Object> map);
}
