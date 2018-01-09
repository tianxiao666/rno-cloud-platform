package com.hgicreate.rno.mapper.gsm;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmDtAnalysisDataMapper {
    /**
     * 获取当前区域下的小区
     * @return
     */
    List<Map<String, Object>> getDistinctCell();

    /**
     * 根据查询的小区获取其覆盖小区
     *
     * @param cellId
     * @return
     */
    List<Map<String, Object>> getCoverageCell(String cellId);

    /**
     * 根据查询的采样点小区获取其覆盖小区
     *
     * @param cellId
     * @return
     */
    List<Map<String, Object>> getCoverageSample(String cellId);

    /**
     * 获取邻区
     *
     * @param longitude
     * @param latitude
     * @return
     */
    List<Map<String, Object>> getNcell(Double longitude, Double latitude);
}
