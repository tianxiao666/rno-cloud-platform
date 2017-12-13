package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmFasAnalysisQueryVM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmFasAnalysisMapper {

    List<Map<String,Object>> queryFasChartData(@Param("fasMeaBegTime")Integer fasMeaBegTime ,
                                               @Param("fasMeaEndTime")Integer fasMeaEndTime,
                                               @Param("cell")String cell);
    
    List<Map<String,Object>> getBscCellByCityId(Long cityId);
}
