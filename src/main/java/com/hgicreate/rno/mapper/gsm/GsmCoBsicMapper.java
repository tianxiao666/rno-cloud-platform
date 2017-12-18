package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.domain.gsm.RnoGsmNcell;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicSchemaQueryVM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmCoBsicMapper {

    List<Map<String,Object>> getCoBsicCellsByAreaId(GsmCoBsicQueryVM vm);

    List<Map<String, Object>> getCoBsicCellsByAreaIdAndBcch(GsmCoBsicQueryVM vm);

    List<RnoGsmNcell> queryCommonNcellByTwoCell(@Param("sourceCell") String sourceCell, @Param("targetCell") String targetCell);

    List<RnoGsmNcell> queryNcell(@Param("sourceCell") String sourceCell, @Param("targetCell")String targetCell);

    List<String> getLonLatsByCells(@Param("sourceCell") String sourceCell,  @Param("targetCell")String targetCell);

    Integer findGsmAreaIdByName(@Param("areaName") String areaName);

    List<Map<String,Object>> queryCoBsicConfigSchema(@Param("cityId") int cityId,@Param("schemaName") String schemaName);

}
