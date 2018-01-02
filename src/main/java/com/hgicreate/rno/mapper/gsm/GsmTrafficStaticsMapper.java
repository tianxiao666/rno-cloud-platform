package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmStsResultVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmTrafficStaticsMapper {

    List<Map<String, Object>> getCellAudioOrDataDescByConfigIds(Map<String, Object> map);

    List<GsmStsResultVM> getStsSpecFieldInSelConfig(Map<String, Object> map);

    List<GsmStsResultVM> selectSpecialCellInSelConfig(Map<String, Object> map);
}
