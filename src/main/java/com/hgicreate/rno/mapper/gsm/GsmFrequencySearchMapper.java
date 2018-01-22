package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yang.ch1
 */
@Mapper
public interface GsmFrequencySearchMapper {

    /**
     * 通过小区参数查询小区详细信息
     * @param cellId 小区id
     * @param cellName 小区名称
     * @param cellEnName 小区英文名
     * @param lac 小区lac值
     * @param ci 小区ci值
     * @param cityId 小区所属区域id
     * @return 小区信息列表
     */
    List<GsmCell> findCellByCondition(@Param("cellId") String cellId,
                                      @Param("cellName") String cellName,
                                      @Param("cellEnName") String cellEnName,
                                      @Param("lac") String lac,
                                      @Param("ci") String ci,
                                      @Param("cityId") String cityId);

    /**
     * 通过小区id获取所有邻区信息
     * @param cellId 小区id
     * @return 小区信息列表
     */
    List<GsmCell> findNcellByCondition(@Param("cellId") String cellId);

    /**
     * 通过bcch和区域id获取小区信息
     * @param bcch 小区bcch频点值
     * @param cityId 小区所属区域id
     * @return 小区信息列表
     */
    List<GsmCell> findCellByBcchAndCityId(@Param("bcch") String bcch,@Param("cityId") String cityId);
}
