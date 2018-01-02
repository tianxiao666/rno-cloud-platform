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

@Slf4j
@RestController
@RequestMapping("/api/gsm-traffic-statics")
public class GsmTrafficStaticsResource {

    @Autowired
    private GsmTrafficStaticsService gsmTrafficStaticsService;

    @PostMapping("/get-cell-performance-quota-list")
    public List<GsmStsConfigVM> gsmTrafficQuery(String data) {
        log.info("传来的参数为={}", data);
        List<Map<String, Object>> configlists = gsmTrafficStaticsService.getCellAudioOrDataDescByConfigIds(data);
        log.debug("查询列表为={}", configlists);
        List<GsmStsConfigVM> planConfiglists = new ArrayList<>();
        for (int i = 0; i < configlists.size(); i++) {
            GsmStsConfigVM stsConfig = new GsmStsConfigVM();
            GsmStsAnaItemDetailVM stsAnaItemDetail = new GsmStsAnaItemDetailVM();
            String CELL_DESCRIPTOR_ID = configlists.get(i).get("STS_DESC_ID").toString();
            String NAME = configlists.get(i).get("NET_TYPE").toString();
            String STS_DATE = configlists.get(i).get("STS_DATE").toString();
            String AREA_ID = configlists.get(i).get("AREA_ID").toString();
            String AREANAME = configlists.get(i).get("AREANAME").toString();
            String SPEC_TYPE = configlists.get(i).get("SPEC_TYPE").toString();
            String STS_PERIOD = configlists.get(i).get("STS_PERIOD").toString();
            stsConfig.setConfigId(Long.parseLong(CELL_DESCRIPTOR_ID));
            stsAnaItemDetail.setAreaId(Long.parseLong(AREA_ID));
            stsAnaItemDetail.setAreaName(AREANAME);
            stsAnaItemDetail.setStsDate(STS_DATE);
            stsAnaItemDetail.setPeriodType(STS_PERIOD);
            stsAnaItemDetail.setStsType(NAME + (SPEC_TYPE.equals("CELLAUDIOINDEX") ? "小区语音业务指标" : "小区数据业务指标"));

            stsConfig.setStsAnaItemDetail(stsAnaItemDetail);

            planConfiglists.add(stsConfig);
        }
        log.info("返回的结果集为={}", planConfiglists);
        return planConfiglists;
    }

    /**
     * 统计资源利用率
     * <p>
     * 输入： stsCode： radioresourcerate:无线资源利用率 accsucrate:接通率 droprate:掉话率
     * dropnum:掉话数 handoversucrate:切换成功率
     * <p>
     * startIndex:起始编号
     * <p>
     * 输出： RnoStsQueryResult的json
     */
    @PostMapping("/statics-resource-utilization-rate")
    public GsmStsQueryResultVM staticsResourceUtilizationRate(String stsCode, int startIndex, String selectedList) {
        log.info("进入方法：staticsRadioResourceUtilizationRate。stsCode={},startIndex={},selectedList={}", stsCode, startIndex, selectedList);
        String[] selectedStringList = selectedList.split(",");
        List<Integer> selectLists = new ArrayList<>();
        for (String one : selectedStringList) {
            selectLists.add(Integer.parseInt(one));
        }
        int size = 1000;// 一次最多传送1000
        List<GsmStsResultVM> stsResults = gsmTrafficStaticsService.staticsResourceUtilizationRateInSelList(stsCode, selectLists);

        log.info("获取到stsCode=" + stsCode + "对应的统计数据：" + (stsResults == null ? stsResults : stsResults.size()));
        boolean hasmore = false;
        int toIndex = startIndex + size;
        int totalCnt = 0;

        GsmStsQueryResultVM queryResult = new GsmStsQueryResultVM();
        if (stsResults == null || stsResults.size() == 0) {

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
        queryResult.setStartIndex(toIndex);// 告诉下一次的起点

        log.info("退出方法：staticsResourceUtilizationRateForAjaxAction。返回：" + queryResult);
        return queryResult;
    }

    /**
     * 统计符合某种要求的小区
     *
     * @author brightming 2013-10-16 下午4:14:55
     */
    @PostMapping("/staticsSpecialCellForAjaxAction")
    public GsmStsQueryResultVM staticsSpecialCellForAjaxAction(String stsCode, int startIndex, String selectedList) {
        log.info("进入方法：staticsSpecialCellForAjaxAction。stsCode={},startIndex={},selectedList={}", stsCode, startIndex, selectedList);

        String[] selectedStringList = selectedList.split(",");
        List<Integer> selectLists = new ArrayList<>();
        for (String one : selectedStringList) {
            selectLists.add(Integer.parseInt(one));
        }
        int size = 1000;// 一次最多传送1000
        List<GsmStsResultVM> stsResults = gsmTrafficStaticsService.staticsSpecialCellInSelList(stsCode, selectLists);

        log.info("获取到cellType={},对应的统计数据={},大小={}", stsCode, stsResults, stsResults.size());
        boolean hasmore = false;
        int toIndex = startIndex + size;
        int totalCnt = 0;

        GsmStsQueryResultVM queryResult = new GsmStsQueryResultVM();
        if (stsResults == null || stsResults.size() == 0) {

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
        queryResult.setStartIndex(toIndex);// 告诉下一次的起点
        return queryResult;
    }
}
