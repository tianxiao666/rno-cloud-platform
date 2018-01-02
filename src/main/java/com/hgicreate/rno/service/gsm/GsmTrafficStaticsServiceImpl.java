package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmTrafficStaticsMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsResultVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GsmTrafficStaticsServiceImpl implements GsmTrafficStaticsService {

    @Autowired
    private GsmTrafficStaticsMapper gsmTrafficStaticsMapper;

    public List<Map<String, Object>> getCellAudioOrDataDescByConfigIds(final String configIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("configIds", configIds);
        return gsmTrafficStaticsMapper.getCellAudioOrDataDescByConfigIds(map);
    }

    public List<GsmStsResultVM> staticsResourceUtilizationRateInSelList(
            String stsCode, List<Integer> selConfigs) {
        log.info("进入staticsResourceUtilizationRateInSelList。 selConfigs="
                + selConfigs + ",stsCode=" + stsCode);

        if (selConfigs == null || selConfigs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (stsCode == null || stsCode.trim().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        System.out.println(selConfigs);
        Collections.sort(selConfigs);
        System.out.println(selConfigs);
        List<GsmStsResultVM> stsResults = null;
        System.out.println(stsCode);
        if (stsResults == null) {
            // 从数据库统计
            String fieldName = "";
            if ("radioresourcerate".equals(stsCode)) {
                fieldName = "RESOURCE_USE_RATE";
            } else if ("accsucrate".equals(stsCode)) {
                fieldName = "ACCESS_OK_RATE";
            } else if ("droprate".equals(stsCode)) {
                fieldName = "RADIO_DROP_RATE_NO_HV";
            } else if ("dropnum".equals(stsCode)) {
                fieldName = "DROP_CALL_NUM_TOGETHER";
            } else if ("handoversucrate".equals(stsCode)) {
                fieldName = "HANDOVER_SUC_RATE";
            }
            StringBuilder buf = new StringBuilder();
            for (int sc : selConfigs) {
                buf.append(sc + ",");
            }
            if (buf.length() > 1) {
                buf.deleteCharAt(buf.length() - 1);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("fieldName", fieldName);
            map.put("buf", buf.toString());
            stsResults = gsmTrafficStaticsMapper.getStsSpecFieldInSelConfig(map);
        }
        return stsResults;
    }

    public List<GsmStsResultVM> staticsSpecialCellInSelList(String needCellType, List<Integer> selConfigs) {
        log.info("进入staticsSpecialCellInSelList。 selConfigs=" + selConfigs
                + ",cellType=" + needCellType);

        if (selConfigs == null || selConfigs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (needCellType == null || needCellType.trim().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Collections.sort(selConfigs);
        StringBuilder buf = new StringBuilder();
        for (int sc : selConfigs) {
            buf.append(sc + ",");
        }
        if (buf.length() > 1) {
            buf.deleteCharAt(buf.length() - 1);
        }

        String sql = "";
        // 超闲小区
        if ("veryidlecell".equals(needCellType)) {
            sql = "select DISTINCT(cell) as cell ,max(TRAFFIC_PER_LINE) as maxValue,avg(TRAFFIC_PER_LINE) as avgValue,min(TRAFFIC_PER_LINE) as minValue,count(TRAFFIC_PER_LINE) as cnt from RNO_GSM_STS  sts INNER JOIN RNO_GSM_STS_DESCRIPTOR  descp on sts.DESCRIPTOR_ID=descp.STS_DESC_ID and DESCP.STS_DESC_ID in ("
                    + buf.toString()
                    + ") and STS.CARRIER_NUMBER>2 GROUP BY cell having (max(TRAFFIC_PER_LINE)<0.15 OR max(PDCH_CARRYING_EFFICIENCY)<3)";
        } else if ("overloadcell".equals(needCellType)) {
            // 超忙小区
            sql = "select DISTINCT(cell),max(TRAFFIC_PER_LINE) maxValue,avg(TRAFFIC_PER_LINE) avgValue,MIN(TRAFFIC_PER_LINE) minValue,COUNT(TRAFFIC_PER_LINE) cnt from RNO_GSM_STS  sts INNER JOIN RNO_GSM_STS_DESCRIPTOR  descp on sts.DESCRIPTOR_ID=descp.STS_DESC_ID AND descp.STS_DESC_ID IN ("
                    + buf.toString()
                    + ") and descp.STS_PERIOD='2000-2100' group by cell having(max(TRAFFIC_PER_LINE)>0.9)";
        } else if ("highuseradiocell".equals(needCellType)) {
            // 高无线利用率小区
            sql = "select DISTINCT(cell),max(RESOURCE_USE_RATE) maxValue,avg(RESOURCE_USE_RATE) avgValue,MIN(RESOURCE_USE_RATE) minValue,COUNT(RESOURCE_USE_RATE) cnt from RNO_GSM_STS  sts INNER JOIN RNO_GSM_STS_DESCRIPTOR  descp on sts.DESCRIPTOR_ID=descp.STS_DESC_ID AND descp.STS_DESC_ID IN ("
                    + buf.toString()
                    + ") and descp.STS_PERIOD='2000-2100' group by cell having(avg(RESOURCE_USE_RATE)>90)";
        } else if ("highcongindatacell".equals(needCellType)) {
            // 数据高拥塞
            sql = "select DISTINCT(cell),max(DOWNLINK_TBF_CONG_RATE) as maxValue,avg(DOWNLINK_TBF_CONG_RATE) as avgValue ,min(DOWNLINK_TBF_CONG_RATE) as minValue,count(DOWNLINK_TBF_CONG_RATE) as cnt FROM  RNO_GSM_STS sts INNER JOIN RNO_GSM_STS_DESCRIPTOR descp on sts.DESCRIPTOR_ID=descp.STS_DESC_ID AND descp.STS_DESC_ID IN ("
                    + buf.toString()
                    + ") AND descp.STS_PERIOD in ('2100-2200','2200-2300','2300-0000') and (1024*STS.DATA_TRAFFIC)>100 group by cell having max(DOWNLINK_TBF_CONG_RATE)>5";
        } else if ("badlyicmcell".equals(needCellType)) {
            sql = "select DISTINCT(cell),max(ICM) AS maxValue,min(ICM) as minValue,avg(ICM) as avgValue,count(ICM) as cnt from RNO_GSM_STS sts INNER JOIN RNO_GSM_STS_DESCRIPTOR descp on sts.DESCRIPTOR_ID=descp.STS_DESC_ID AND descp.STS_DESC_ID IN ("
                    + buf.toString() + ") group by cell having (avg(ICM)>30)";
        }
        if (!"".equals(sql)) {
            // 从数据库统计
            List<GsmStsResultVM> stsResults = null;
            Map<String, Object> map = new HashMap<>();
            map.put("sql", sql);
            stsResults = gsmTrafficStaticsMapper.selectSpecialCellInSelConfig(map);
            log.debug("结果集={}", stsResults);
            return stsResults;
        } else {
            return null;
        }
    }
}
