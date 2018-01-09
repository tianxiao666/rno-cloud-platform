package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmTrafficStaticsService;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsAnaItemDetailVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsConfigVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsQueryResultVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsResultVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiao.sz
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-traffic-statics")
public class GsmTrafficStaticsResource {

    @Autowired
    private GsmTrafficStaticsService gsmTrafficStaticsService;

    /**
     * @param data 来自页面的id串
     * @return 返回任务配置列表
     */
    @PostMapping("/get-cell-performance-quota-list")
    public List<GsmStsConfigVM> gsmTrafficQuery(String data) {
        log.info("传来的参数为={}", data);
        List<Map<String, Object>> configlists = gsmTrafficStaticsService.getCellAudioOrDataDescByConfigIds(data);
        log.debug("查询列表为={}", configlists);
        List<GsmStsConfigVM> planConfiglists = new ArrayList<>();
        for (Map<String, Object> oneConfig : configlists) {
            GsmStsConfigVM stsConfig = new GsmStsConfigVM();
            GsmStsAnaItemDetailVM stsAnaItemDetail = new GsmStsAnaItemDetailVM();
            String stsDescId = oneConfig.get("STS_DESC_ID").toString();
            String netType = oneConfig.get("NET_TYPE").toString();
            String stsDate = oneConfig.get("STS_DATE").toString();
            String areaId = oneConfig.get("AREA_ID").toString();
            String areaName = oneConfig.get("AREANAME").toString();
            String specType = oneConfig.get("SPEC_TYPE").toString();
            String stsPeriod = oneConfig.get("STS_PERIOD").toString();
            stsConfig.setConfigId(Long.parseLong(stsDescId));
            stsAnaItemDetail.setAreaId(Long.parseLong(areaId));
            stsAnaItemDetail.setAreaName(areaName);
            stsAnaItemDetail.setStsDate(stsDate);
            stsAnaItemDetail.setPeriodType(stsPeriod);
            stsAnaItemDetail.setStsType(netType + ("CELLAUDIOINDEX".equals(specType) ? "小区语音业务指标" : "小区数据业务指标"));

            stsConfig.setStsAnaItemDetail(stsAnaItemDetail);

            planConfiglists.add(stsConfig);
        }
        log.info("返回的结果集为={}", planConfiglists);
        return planConfiglists;
    }

    /**
     * 统计资源利用率
     *
     * @param stsCode      点击的按钮的操作识别码
     * @param startIndex   开始序号
     * @param selectedList 列表的勾选结果集
     * @return GsmStsQueryResultVM
     */
    @PostMapping("/statics-resource-utilization-rate")
    public GsmStsQueryResultVM staticsResourceUtilizationRate(String stsCode, int startIndex, String selectedList) {
        log.info("进入方法：staticsRadioResourceUtilizationRate。stsCode={},startIndex={},selectedList={}", stsCode, startIndex, selectedList);
        String[] selectedStringList = selectedList.split(",");
        List<Integer> selectLists = new ArrayList<>();
        for (String one : selectedStringList) {
            selectLists.add(Integer.parseInt(one));
        }
        // 一次最多传送1000
        int size = 1000;
        List<GsmStsResultVM> stsResults = gsmTrafficStaticsService.staticsResourceUtilizationRateInSelList(stsCode, selectLists);

        log.info("获取到stsCode=" + stsCode + "对应的统计数据：" + (stsResults == null ? stsResults : stsResults.size()));
        boolean hasMore = false;
        int toIndex = startIndex + size;
        int totalCnt = 0;

        GsmStsQueryResultVM queryResult = new GsmStsQueryResultVM();
        if (stsResults != null) {
            List<GsmStsResultVM> subList = null;
            totalCnt = stsResults.size();
            if (toIndex >= stsResults.size()) {
                toIndex = stsResults.size();
                subList = stsResults.subList(startIndex, toIndex);
            } else {
                subList = stsResults;
                hasMore = true;
            }
            queryResult.setRnoStsResults(subList);
        }
        queryResult.setHasMore(hasMore);
        queryResult.setTotalCnt(totalCnt);
        // 告诉下一次的起点
        queryResult.setStartIndex(toIndex);

        log.info("退出方法：staticsResourceUtilizationRateForAjaxAction。返回：" + queryResult);
        return queryResult;
    }

    /**
     * 统计符合某种要求的小区
     *
     * @author xiao.sz
     */
    @PostMapping("/staticsSpecialCellForAjaxAction")
    public GsmStsQueryResultVM staticsSpecialCellForAjaxAction(String stsCode, int startIndex, String selectedList) {
        log.info("进入方法：staticsSpecialCellForAjaxAction。stsCode={},startIndex={},selectedList={}", stsCode, startIndex, selectedList);

        String[] selectedStringList = selectedList.split(",");
        List<Integer> selectLists = new ArrayList<>();
        for (String one : selectedStringList) {
            selectLists.add(Integer.parseInt(one));
        }
        // 一次最多传送1000
        int size = 1000;
        List<GsmStsResultVM> stsResults = gsmTrafficStaticsService.staticsSpecialCellInSelList(stsCode, selectLists);

        log.info("获取到cellType={},对应的统计数据={},大小={}", stsCode, stsResults, stsResults.size());
        boolean hasmore = false;
        int toIndex = startIndex + size;
        int totalCnt = 0;

        GsmStsQueryResultVM queryResult = new GsmStsQueryResultVM();
        if (stsResults.size() == 0) {

        } else {
            List<GsmStsResultVM> subList = null;
            totalCnt = stsResults.size();
            if (toIndex >= stsResults.size()) {
                toIndex = stsResults.size();
                subList = stsResults.subList(startIndex, toIndex);
            } else {
                subList = stsResults;
                hasmore = true;
            }
            queryResult.setRnoStsResults(subList);
        }
        queryResult.setHasMore(hasmore);
        queryResult.setTotalCnt(totalCnt);
        // 告诉下一次的起点
        queryResult.setStartIndex(toIndex);
        return queryResult;
    }
}
