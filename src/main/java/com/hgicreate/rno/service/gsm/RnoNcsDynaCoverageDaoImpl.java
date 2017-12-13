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

    @Autowired
    private GsmDynamicCoverageMapper gsmDynamicCoverageMapper;
    @Autowired
    private AuthDsDataDaoImpl authDsDataDao;


    /**
     * 检查小区是华为还是爱立信
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    @Override
    public String checkCellIsHwOrEri(final String cell) {
        Map<String, Object> map = new HashMap<>();
        map.put("cell", cell);
        List<Map<String, Object>> cellDataList = gsmDynamicCoverageMapper.selectManufacturersFromRnoBsc(map);
        String result = null;
        if (cellDataList.size() > 0) {
            result = cellDataList.get(0).get("DESC_ID").toString();
        }
        return result;
    }

    /**
     * 查询爱立信ncs数据，并整理得到需要结果
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryEriDataFromOracle(final long cityId,
                                                            final String cell, final String startDate, final String endDate, String RELSS) {

        String startTime = String.format(startDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        String endTime = String.format(endDate + " 00:00:00", "yyyy-MM-dd HH:mi:ss");
        Map<String, Object> map = new HashMap<>();
        map.put("cityId", "'" + cityId + "'");
        map.put("startTime", "'" + startTime + "'");
        map.put("endTime", "'" + endTime + "'");
        List<Map<String, Object>> eriNcsDescInfos = ncsMapper.selectfromRnoRnoGSMEriNcsDescripter(map);
        String ncsFields = "CELL,NCELL,CELL_LON,CELL_LAT,NCELL_LON,NCELL_LAT,REPARFCN,TIMESRELSS,TIMESRELSS2,TIMESRELSS3,TIMESRELSS4,TIMESRELSS5,DISTANCE,INTERFER";
        String TIMESRELSS = "";
        String ncsDescId = "";
        String areaIdStr = authDsDataDao
                .getSubAreaAndSelfIdListStrByParentId(cityId);
        log.debug("areaIdStr==={}", areaIdStr);
        log.debug("eriNcsDescInfos==={}", eriNcsDescInfos.get(0));
        for (Map<String, Object> ncsDesc : eriNcsDescInfos) {

            ncsDescId = ncsDesc.get("DESC_ID").toString();

            //确定门限值字段
            TIMESRELSS = getEriTimesRelssXByValue((Map<String, Object>) ncsDesc, RELSS);
            log.debug("TIMESRELSS === {}", TIMESRELSS);
            //log.debug("动态覆盖图：获取[" + RELSS + "]对应的列为：" + TIMESRELSS);
            if (TIMESRELSS == null || "".equals(TIMESRELSS)) {
                log.debug("动态覆盖图：ncs[" + ncsDescId + "]未获取到相应的[" + RELSS + "]对应的列，将尝试用+0获取");
                TIMESRELSS = getEriTimesRelssXByValue((Map<String, Object>) ncsDesc, "+0");
                log.debug("动态覆盖图：获取[+0]对应的列为：" + TIMESRELSS);
            }
            if ("".equals(TIMESRELSS)) {
                continue;
            }
            Map<String, Object> insertMaps = new HashMap<>();
            insertMaps.put("ncsFields", ncsFields);
            insertMaps.put("cell", "'" + cell + "'");
            insertMaps.put("ncsDescId", "'" + ncsDescId + "'");
            insertMaps.put("areaIdStr", areaIdStr);

            ncsMapper.insertIntoRno2GNcsCoverT(insertMaps);

            Map<String, Object> updateMaps = new HashMap<>();
            updateMaps.put("TIMESRELSS", TIMESRELSS);
            updateMaps.put("ncsDescId", ncsDescId);
            ncsMapper.updateRno2GNcsCoverTSetInterfer(updateMaps);
        }

        List<Map<String, Object>> rss = ncsMapper.selectInterferCelllonCelllatCellNcelllonNcelllatFromRno2GNcsCoverT();

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        map = null;
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
//		log.debug("进入getTimesRelssXByValue(rnoncsdesc=" + desc
//				+ ",relsscons=" + relsscons + ")");
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
//		log.debug("退出getTimesRelssXByValue　TIMESRELSS:" + TIMESRELSS);
        return TIMESRELSS;
    }

    /**
     * 查询华为ncs数据，并整理得到需要结果
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryHwDataFromOracle(final long cityId,
                                                           final String cell, final String startDate, final String endDate, final String RELSS) {

        final String areaIdStr = authDsDataDao
                .getSubAreaAndSelfIdListStrByParentId(cityId);

        Map<String, Object> maps = new HashMap<>();
        maps.put("cityId", cityId);
        maps.put("endDate", endDate);
        List<Map<String, Object>> hwNcsDescInfos = ncsMapper.selectIdFromRno2GHwNcsDesc(maps);

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
