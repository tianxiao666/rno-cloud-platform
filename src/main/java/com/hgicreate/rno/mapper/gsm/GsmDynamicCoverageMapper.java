package com.hgicreate.rno.mapper.gsm;

import org.apache.ibatis.annotations.Mapper;

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

    List<Map<String, Object>> selectFromRnoGSMEriNcsDescripter(Map<String, Object> map);

    List<Map<String, Object>> getNcellDetailsByCellandCityId(Map<String, Object> map);
}
