package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.DynamicCoverageData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmDynamicCoverageMapper {


    void insertIntoRno2GNcsCoverT(Map<String, Object> map);

    void updateRno2GNcsCoverTSetInterfer(Map<String, Object> map);

    List<Map<String, Object>> selectInterferCelllonCelllatCellNcelllonNcelllatFromRno2GNcsCoverT();

    void deleteAll();

    List<Map<String, Object>> selectIdFromRno2GHwNcsDesc(Map<String, Object> map);

    List<Map<String, Object>> selectValstrCelllonCelllatCellNcelllonNcelllatFrom(Map<String, Object> maps);

    List<Map<String, Object>> selectManufacturersFromRnoBsc(Map<String, Object> map);

    List<Map<String, Object>> selectAllFromRnoGSMMapLngLatRelaGps(Map<String, Object> map);

    List<Map<String, Object>> selectfromRnoRnoGSMEriNcsDescripter(Map<String, Object> map);

    List<Map<String, Object>> selectFromSysAreaOrderByParentId();

    /*          动态覆盖             */

    /**
     * 查询MR in 干扰数据
     */
    List<DynamicCoverageData> queryDynamicCoverageInData(@Param("cityId") long cityId, @Param("lteCellId") String lteCellId, @Param("dates") String dates);

    /**
     * 查询MR OUT 干扰数据
     */
    List<DynamicCoverageData> queryDynamicCoverageOutData(@Param("cityId") long cityId, @Param("lteCellId") String lteCellId, @Param("dates") String dates);

    /*          PCI 评估             */

    /**
     * 获取IN干扰小区信息
     */
    List<Map<String, Object>> getInCellInfo(Map<String, Object> map);

    /**
     * 获取OUT干扰小区信息
     */
    List<Map<String, Object>> getOutCellInfo(Map<String, Object> map);

    /**
     * 获取PCI智能优化小区信息
     */
    List<Map<String, Object>> getPciCellInfo(Map<String, Object> map);
}
