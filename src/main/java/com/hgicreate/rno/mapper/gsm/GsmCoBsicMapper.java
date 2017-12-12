package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.domain.gsm.GsmNcell;
import com.hgicreate.rno.domain.gsm.GsmNcellRelation;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCoBsicQueryVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmCoBsicMapper {

    List<Map<String,Object>> getCoBsicCellsByAreaId(GsmCoBsicQueryVM vm);

    List<Map<String, Object>> getCoBsicCellsByAreaIdAndBcch(GsmCoBsicQueryVM vm);

    List<GsmNcellRelation> queryCommonNcellByTwoCell(String sourceCell, String targetCell);

    List<GsmNcellRelation> queryNcell(String sourceCell, String targetCell);

    List<String> getLonLatsByCells(String sourceCell, String targetCell);


}
