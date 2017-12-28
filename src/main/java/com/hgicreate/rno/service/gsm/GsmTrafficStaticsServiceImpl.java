package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmTrafficStaticsMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsConfigVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsResultVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GsmTrafficStaticsServiceImpl implements GsmTrafficStaticsService {

//    private static Comparator stsConfigCompartor = new Comparator<GsmStsConfigVM>() {
//        public int compare(GsmStsConfigVM o1, GsmStsConfigVM o2) {
//            if (o1 == null || o2 == null) {
//                return 0;
//            }
//            if (o1.getConfigId() > o2.getConfigId()) {
//                return 1;
//            }
//            return -1;
//        }
//    };
    @Autowired
    private GsmTrafficStaticsMapper gsmTrafficStaticsMapper;

    /**
     * 改造：通过小区语音和数据话统配置ID的字符串获取话统指标描述数据
     *
     * @param configIds
     * @return
     * @author chao.xj
     * @date 2013-12-10下午05:46:52
     */
    public List<Map<String, Object>> getCellAudioOrDataDescByConfigIds(final String configIds) {
        Map<String, Object>map = new HashMap<>();
        map.put("configIds",configIds);
        return gsmTrafficStaticsMapper.getCellAudioOrDataDescByConfigIds(map);
    }

    public List<Map<String, Object>> getRnoTraffic(String trafficCode){
        Map<String, Object>map = new HashMap<>();
        map.put("trafficCode","'"+trafficCode+"'");
        return gsmTrafficStaticsMapper.getAllFromRnoTrafficIndexType(map);
    }

    public List<Map<String, Object>> getRnoTrafficRendererByTrafficCodeAndAreaId(String trafficCode,int areaId){
        Map<String, Object>map = new HashMap<>();
        map.put("trafficCode","'"+trafficCode+"'");
        map.put("areaId",areaId);
        return gsmTrafficStaticsMapper.getAllFromRnoTrafficIndexTypeAndRnoTrafficRendererConfig(map);
    }

    public List<Map<String, Object>> getDefaultRnoTrafficRenderer(String trafficCode){
        return getRnoTrafficRendererByTrafficCodeAndAreaId(trafficCode,-1);
    }

    /**
     * 统计指定类型的利用率或数量
     *
     * @param stsCode
     * @param selConfigs
     * @return
     * @author brightming 2013-10-14 上午11:09:38
     */
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
            String fieldName="";
//            accsucrate=ACCESS_OK_RATE
//            droprate=RADIO_DROP_RATE_NO_HV
//            dropnum=DROP_CALL_NUM_TOGETHER
//            handoversucrate=HANDOVER_SUC_RATE
            if("radioresourcerate".equals(stsCode)){
                fieldName = "RESOURCE_USE_RATE";
            }else if("accsucrate".equals(stsCode)){
                fieldName = "ACCESS_OK_RATE";
            }else if("droprate".equals(stsCode)){
                fieldName = "RADIO_DROP_RATE_NO_HV";
            }else if("dropnum".equals(stsCode)){
                fieldName = "DROP_CALL_NUM_TOGETHER";
            }else if("handoversucrate".equals(stsCode)){
                fieldName = "HANDOVER_SUC_RATE";
            }
            StringBuilder buf = new StringBuilder();
            for (int sc : selConfigs) {
                buf.append(sc + ",");
            }
            if (buf.length() > 1) {
                buf.deleteCharAt(buf.length() - 1);
            }
            Map<String, Object>map = new HashMap<>();
            map.put("fieldName",fieldName);
            map.put("buf",buf.toString());
            stsResults = gsmTrafficStaticsMapper.getStsSpecFieldInSelConfig(map);
        }

        return stsResults;
    }

    /**
     * 获取某类型的小区
     *
     * @param cellType
     * @param selConfigs
     * @return
     * @author brightming 2013-10-16 下午4:17:34
     */
    public List<GsmStsResultVM> staticsSpecialCellInSelList(String cellType,List<Integer> selConfigs) {
        log.info("进入staticsSpecialCellInSelList。 selConfigs=" + selConfigs
                + ",cellType=" + cellType);

        if (selConfigs == null || selConfigs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        if (cellType == null || cellType.trim().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Collections.sort(selConfigs);
        List<GsmStsResultVM> stsResults = null;
        // 从数据库统计
        Map<String, Object>map = new HashMap<>();
        map.put("selConfigs",selConfigs);
        map.put("cellType",cellType);
        stsResults = gsmTrafficStaticsMapper.selectSpecialCellInSelConfig(map);

        return stsResults;
    }
}
