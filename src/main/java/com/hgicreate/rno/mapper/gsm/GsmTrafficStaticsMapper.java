package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmStsResultDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author xiao.sz
 */
@Mapper
public interface GsmTrafficStaticsMapper {

    List<Map<String, Object>> getCellAudioOrDataDescByConfigIds(Map<String, Object> map);

    List<GsmStsResultDTO> getStsSpecFieldInSelConfig(Map<String, Object> map);

    List<GsmStsResultDTO> selectSpecialCellInSelConfig(Map<String, Object> map);
}
