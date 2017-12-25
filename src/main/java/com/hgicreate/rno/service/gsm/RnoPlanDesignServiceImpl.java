package com.hgicreate.rno.service.gsm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hgicreate.rno.mapper.gsm.GsmFrequencyReuseMapper;
import com.hgicreate.rno.web.rest.gsm.vm.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class RnoPlanDesignServiceImpl implements RnoPlanDesignService {
    private static Gson gson = new GsonBuilder().create();// 线程安全

    @Autowired
    private GsmFrequencyReuseMapper gsmFrequencyReuseMapper;

    /**
     * @param selectConfig
     * @param page
     * @return GisCellQueryResult
     * @description: 根据类型获取选择的小区配置或小区干扰中分析列表中的小区的gis信息
     * @author：yuan.yw
     * @date：Nov 8, 2013 10:08:37 AM
     */
    public GsmGisCellQueryResultVM getFreqReuseCellGisInfoFromSelectionList(
            GsmPlanConfigVM selectConfig, GsmPageVM page) {
        log.info("进入getFreqReuseCellGisInfoFromSelectionList。 selectConfig="
                + selectConfig + ",page=" + page);

        List<GsmGisCellVM> gisCells = null;
        if (selectConfig == null) {
            return null;
        }

        GsmGisCellQueryResultVM result = new GsmGisCellQueryResultVM();
        result.setTotalCnt(0);

        if (gisCells == null) {
            // 从数据库加载
            boolean isTemp = selectConfig.isTemp();
            log.info("isTemp:" + isTemp);
            String cellTable = "cell";
            if (isTemp) {
                cellTable = "rno_temp_cell";// 临时配置小区
            }
            String cellId = selectConfig.getConfigId();
            Map<String, Object> map = new HashMap<>();
            map.put("cellTable", cellTable);
            map.put("configId", "'"+cellId+"'");
            gisCells = gsmFrequencyReuseMapper
                    .selectFreqReuseCellGisInfoFromSelectionList(map);
        }
        if (gisCells != null) {
            result.setTotalCnt(gisCells.size());
        }
        if (page != null) {
            int start = page.calculateStart();
            if (start > gisCells.size()) {
                start = gisCells.size() - 1;
            }
            if (start < 0) {
                start = 0;
            }
            int toIndex = start + page.getPageSize();
            if (toIndex > gisCells.size()) {
                toIndex = gisCells.size();
            }
            gisCells = gisCells.subList(start, toIndex);
        }

        result.setGisCells(gisCells);
        log.info("退出getFreqReuseCellGisInfoFromSelectionList。 返回：" + gisCells);
        return result;
    }

    @Override
    public Map<Integer, Object> staticsFreqReuseInfoInArea(GsmPlanConfigVM selectConfig) {
        Map<Integer, Object> resultMap = null;// 返回结果resultMap
        if (selectConfig == null) {
            return null;
        }
        if (resultMap == null) {
            boolean isTemp = selectConfig.isTemp();
            log.info("isTemp:"+isTemp);
            String cellTable = "cell";
            if (isTemp) {
                cellTable = "rno_temp_cell";// 临时配置小区
            }
            String cellId = selectConfig.getConfigId();
            Map<String, Object>m = new HashMap<>();
            m.put("cellTable",cellTable);
            m.put("cellId","'"+cellId+"'");
            List<Map<String, Object>> freqReuseLists = gsmFrequencyReuseMapper.selectBcchTchFrom(m);

            if (freqReuseLists == null || !freqReuseLists.isEmpty()) {
                resultMap = new TreeMap<Integer, Object>();// 返回结果resultMap
                // treeMap 排序
                for (Map<String, Object> map : freqReuseLists) {// 遍历结果
                    System.out.println(map);
                    String bcch = map.get("BCCH") + "";// bcch 频点
                    String tch = map.get("TCH") + "";// tch 频点
                    if (!"null".equals(bcch) && !"".equals(bcch)) {// bcch 频点复用
                        int key = Integer.valueOf(bcch);
                        if (resultMap.containsKey(key)) {
                            GsmFrequencyReuseInfoVM freInfo = (GsmFrequencyReuseInfoVM) resultMap
                                    .get(key);
                            freInfo.setBcchCount(freInfo.getBcchCount() + 1);
                        } else {
                            GsmFrequencyReuseInfoVM freInfo = new GsmFrequencyReuseInfoVM();
                            freInfo.setFreq(Integer.valueOf(bcch));
                            freInfo.setBcchCount(1);
                            freInfo.setTchCount(0);
                            resultMap.put(freInfo.getFreq(), freInfo);
                        }
                    }
                    if (!"null".equals(tch) && !"".equals(tch)) {// tch 频点复用
                        if (tch != null && !"".equals(tch)) {
                            String[] tchArr = tch.split(",");
                            for (String tchValue : tchArr) {
                                int key = Integer.valueOf(tchValue);
                                if (resultMap.containsKey(key)) {
                                    GsmFrequencyReuseInfoVM freInfo = (GsmFrequencyReuseInfoVM) resultMap
                                            .get(key);
                                    freInfo.setTchCount(freInfo.getTchCount() + 1);
                                } else {
                                    GsmFrequencyReuseInfoVM freInfo = new GsmFrequencyReuseInfoVM();
                                    freInfo.setFreq(Integer.valueOf(tchValue));
                                    freInfo.setTchCount(1);
                                    freInfo.setBcchCount(0);
                                    resultMap.put(freInfo.getFreq(), freInfo);
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultMap;
    }

}
