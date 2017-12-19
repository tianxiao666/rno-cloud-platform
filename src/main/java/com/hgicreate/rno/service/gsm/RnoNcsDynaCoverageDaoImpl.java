package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmDynamicCoverageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RnoNcsDynaCoverageDaoImpl implements RnoNcsDynaCoverageDao {

    @Autowired
    private GsmDynamicCoverageMapper ncsMapper;

    /**
     * 检查小区是华为还是爱立信
     */
    @Override
    public String checkCellIsHwOrEri(final String enName) {
        log.debug("enName ===={}", enName);
        Map<String, Object> map = new HashMap<>();
        map.put("enName", enName);
        List<Map<String, Object>> cellDataList = ncsMapper.selectManufacturersFromRnoBsc(map);
        String result = null;
        if (cellDataList.size() > 0) {
            result = cellDataList.get(0).get("DESC_ID").toString();
        }
        return result;
    }

    /**
     * 查询爱立信ncs数据，并整理得到需要结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryEriDataFromOracle(final long cityId,
                                                            final String enName, String cellId, final String startDate, final String endDate, String RELSS) {

        String startTime = String.format(startDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        String endTime = String.format(endDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", "'" + cityId + "'");
        map.put("startTime", "'" + startTime + "'");
        map.put("endTime", "'" + endTime + "'");
        List<Map<String, Object>> eriNcsDescInfos = ncsMapper.selectfromRnoRnoGSMEriNcsDescripter(map);
        if(eriNcsDescInfos.size()<=0){
            return null;
        }
        String ncsFields = "CELL,NCELL,CELL_LON,CELL_LAT,NCELL_LON,NCELL_LAT,REPARFCN,TIMESRELSS,TIMESRELSS2,TIMESRELSS3,TIMESRELSS4,TIMESRELSS5,DISTANCE,INTERFER";
        String TIMESRELSS = "";
        String ncsDescId = "";
        Map<String, Object> ncellMap = new HashMap<>();
        ncellMap.put("cellId", "'" + cellId + "'");
        ncellMap.put("areaId", cityId);
        List<Map<String, Object>> gisCells = ncsMapper.getNcellDetailsByCellandCityId(ncellMap);
        log.debug("gisCells==={}", gisCells);
        String areaIdStr = "";
        for (Map<String, Object> oneCell : gisCells) {
            areaIdStr += oneCell.get("AREA_ID") + ",";
        }
        if (areaIdStr.length() > 0) {
            areaIdStr = areaIdStr.substring(0, areaIdStr.length() - 1);
        }
        log.debug("eriNcsDescInfos==={}", eriNcsDescInfos.get(0));
        //先清空临时表
        ncsMapper.deleteAll();

        Map<String, Object> insertMaps = new HashMap<>();
        insertMaps.put("ncsFields", ncsFields);
        insertMaps.put("cellId", "'" + cellId + "'");
        insertMaps.put("areaIdStr", areaIdStr);
        insertMaps.put("cityId", cityId);
        insertMaps.put("startTime", "'" + startTime + "'");
        insertMaps.put("endTime", "'" + endTime + "'");
        ncsMapper.insertIntoRno2GNcsCoverT(insertMaps);

        for (Map<String, Object> ncsDesc : eriNcsDescInfos) {

            ncsDescId = ncsDesc.get("DESC_ID").toString();
            //确定门限值字段
            TIMESRELSS = getEriTimesRelssXByValue(ncsDesc, RELSS);
            log.debug("TIMESRELSS === {}", TIMESRELSS);
            if (TIMESRELSS == null || "".equals(TIMESRELSS)) {
                log.debug("动态覆盖图：ncs[" + ncsDescId + "]未获取到相应的[" + RELSS + "]对应的列，将尝试用+0获取");
                TIMESRELSS = getEriTimesRelssXByValue(ncsDesc, "+0");
                log.debug("动态覆盖图：获取[+0]对应的列为：" + TIMESRELSS);
            }
            if ("".equals(TIMESRELSS)) {
                continue;
            }
            Map<String, Object> updateMaps = new HashMap<>();
            updateMaps.put("TIMESRELSS", TIMESRELSS);
            updateMaps.put("ncsDescId", ncsDescId);
            updateMaps.put("cellId", "'" + cellId + "'");
            updateMaps.put("areaIdStr", areaIdStr);
            ncsMapper.updateRno2GNcsCoverTSetInterfer(updateMaps);
        }
        List<Map<String, Object>> rss = ncsMapper.selectInterferCelllonCelllatCellNcelllonNcelllatFromRno2GNcsCoverT();

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> oneRss : rss) {
            map = new HashMap<String, Object>();
            map.put("VAL", oneRss.get("VAL"));
            map.put("LNG", oneRss.get("LNG"));
            map.put("LAT", oneRss.get("LAT"));
            map.put("NCELL_ID", oneRss.get("NCELL_ID"));
            map.put("CELL_LNG", oneRss.get("CELL_LNG"));
            map.put("CELL_LAT", oneRss.get("CELL_LAT"));
            result.add(map);
        }
        ncsMapper.deleteAll();
        return result;
    }

    /**
     * 根据限值获取爱立信ncs中对应的timesrelss
     *
     * @param desc
     * @param relsscons
     * @return
     */
    public String getEriTimesRelssXByValue(Map<String, Object> desc,
                                           String relsscons) {
        log.debug("进入getTimesRelssXByValue(rnoncs desc=" + desc
                + ",relsscons=" + relsscons + ")");
        String TIMESRELSS = "";
        String relss;
        relss = (Integer.parseInt(desc.get("RELSS_SIGN").toString()) == 0 ? "+" : "-")
                + desc.get("RELSS").toString();
        if (relsscons.equals(relss)) {
            TIMESRELSS = "TIMESRELSS";
        } else {
            relss = (Integer.parseInt(desc.get("RELSS2_SIGN").toString()) == 0 ? "+" : "-")
                    + desc.get("RELSS2").toString();
            if (relsscons.equals(relss)) {
                TIMESRELSS = "TIMESRELSS2";
            } else {
                relss = (Integer.parseInt(desc.get("RELSS3_SIGN").toString()) == 0 ? "+" : "-")
                        + desc.get("RELSS3").toString();
                if (relsscons.equals(relss)) {
                    TIMESRELSS = "TIMESRELSS3";
                } else {
                    relss = (Integer.parseInt(desc.get("RELSS4_SIGN").toString()) == 0 ? "+" : "-")
                            + desc.get("RELSS4").toString();
                    if (relsscons.equals(relss)) {
                        TIMESRELSS = "TIMESRELSS4";
                    } else {
                        relss = (Integer.parseInt(desc.get("RELSS5_SIGN").toString()) == 0 ? "+" : "-")
                                + desc.get("RELSS5").toString();
                        if (relsscons.equals(relss)) {
                            TIMESRELSS = "TIMESRELSS5";
                        }
                    }
                }
            }
        }
        return TIMESRELSS;
    }

    /**
     * 查询华为ncs数据，并整理得到需要结果
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryHwDataFromOracle(final long cityId,
                                                           final String cell, String cellId, final String startDate, final String endDate, final String RELSS) {

        String startTime = String.format(startDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        String endTime = String.format(endDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        Map<String, Object> maps = new HashMap<>();
        maps.put("cityId", "'" + cityId + "'");
        maps.put("startTime", startTime);
        maps.put("endTime", endTime);
        List<Map<String, Object>> hwNcsDescInfos = ncsMapper.selectIdFromRno2GHwNcsDesc(maps);

        Map<String, Object> ncellMap = new HashMap<>();
        ncellMap.put("cellId", "'" + cellId + "'");
        ncellMap.put("areaId", cityId);
        List<Map<String, Object>> gisCells = ncsMapper.getNcellDetailsByCellandCityId(ncellMap);
        log.debug("gisCells==={}", gisCells);
        String areaIdStr = "";
        for (Map<String, Object> oneCell : gisCells) {
            areaIdStr += oneCell.get("AREA_ID") + ",";
        }
        if (areaIdStr.length() > 0) {
            areaIdStr = areaIdStr.substring(0, areaIdStr.length() - 1);
        }

        StringBuilder descIdStr = new StringBuilder();
        for (Map<String, Object> map : hwNcsDescInfos) {
            descIdStr.append(map.get("DESC_ID").toString() + ",");
        }
        if (("").equals(descIdStr)) {
            log.debug("cityId=" + cityId + ",cell=" + cell + ",startDate="
                    + startDate + ",endDate=" + endDate + ",找不到对应的华为ncs描述信息！");
            return Collections.emptyList();
        }
        final String descIds = descIdStr.substring(0, descIdStr.length() - 1);

        String valStr = "";
        if ("-12".equals(RELSS)) {
            valStr = "(s361-s369)/s3013 as val,";
        } else {
            valStr = "(s361-s366)/s3013 as val,";
        }
        maps = new HashMap<>();
        maps.put("valStr", valStr);
        maps.put("cell", cell);
        maps.put("descIds", descIds);
        maps.put("areaIdStr", areaIdStr);
        List<Map<String, Object>> rows = ncsMapper.selectValstrCelllonCelllatCellNcelllonNcelllatFrom(maps);

        return rows;
    }
}
