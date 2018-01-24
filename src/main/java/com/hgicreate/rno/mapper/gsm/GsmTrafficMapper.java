package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmTrafficQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmTrafficMapper {

    /**
     * 小区语音业务指标查询与小区数据业务指标查询
     * @param vm 小区业务指标查询vm
     * @return 小区业务指标查询DTO
     */
    List<GsmTrafficQueryDTO> gsmTrafficQuery(GsmTrafficQueryVM vm);

    List<Map<String,Object>> busyCellQuery(Long areaId);
    List<Map<String,Object>> idleCellQuery(Long areaId);
}
