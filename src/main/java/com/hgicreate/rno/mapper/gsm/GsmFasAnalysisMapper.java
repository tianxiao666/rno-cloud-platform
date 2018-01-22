package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmFasAnalysisQueryVM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author yang.ch1
 */
@Mapper
public interface GsmFasAnalysisMapper {

    /**
     * 通过测量开始和结束时间、小区id查询fas频点信息
     * @param fasMeaBegTime fas测量开始时间
     * @param fasMeaEndTime fas测量结束时间
     * @param cell fas小区id
     * @return fas频点数据列表
     */
    List<Map<String,Object>> queryFasChartData(@Param("fasMeaBegTime")String fasMeaBegTime ,
                                               @Param("fasMeaEndTime")String fasMeaEndTime,
                                               @Param("cell")String cell);

}
