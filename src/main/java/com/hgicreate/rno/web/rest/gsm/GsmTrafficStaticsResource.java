package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.repository.gsm.GsmTrafficStaticsRepository;
import com.hgicreate.rno.service.gsm.GsmTrafficStaticsService;
import com.hgicreate.rno.service.gsm.dto.GsmStsConfigDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsAnaItemDetailVM;
import com.hgicreate.rno.service.gsm.dto.GsmStsQueryResultDTO;
import com.hgicreate.rno.service.gsm.dto.GsmStsResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Autowired
    private GsmTrafficStaticsRepository gsmTrafficStaticsRepository;

    /**
     * 返回id 字符串，以供页面获取小区性能指标列表
     *
     * @return 返回id 字符串
     */
    @GetMapping("/id-string")
    public String gsmTrafficId() {
        return gsmTrafficStaticsRepository.getIdString();
    }

    /**
     * 根据id 获取小区性能指标列表
     *
     * @param data 来自页面的id串
     * @return 返回任务配置列表
     */
    @PostMapping("/cell-performance-quota-list")
    public List<GsmStsConfigDTO> gsmTrafficQuery(String data) {
        log.debug("参数:{}", data);
        List<Map<String, Object>> configLists = gsmTrafficStaticsService.getCellAudioOrDataDescByConfigIds(data);
        log.debug("查询列表:{}", configLists);
        List<GsmStsConfigDTO> planConfigLists = new ArrayList<>();
        GsmStsAnaItemDetailVM stsAnaItemDetail;
        for (Map<String, Object> oneConfig : configLists) {
            GsmStsConfigDTO stsConfig = new GsmStsConfigDTO();
            stsAnaItemDetail = new GsmStsAnaItemDetailVM();
            stsConfig.setConfigId(Long.parseLong(oneConfig.get("STS_DESC_ID").toString()));
            stsAnaItemDetail.setAreaId(Long.parseLong(oneConfig.get("AREA_ID").toString()));
            stsAnaItemDetail.setAreaName(oneConfig.get("AREANAME").toString());
            stsAnaItemDetail.setStsDate(oneConfig.get("STS_DATE").toString());
            stsAnaItemDetail.setPeriodType(oneConfig.get("STS_PERIOD").toString());
            stsAnaItemDetail.setStsType(oneConfig.get("NET_TYPE").toString() + ("CELLAUDIOINDEX".equals(oneConfig.get("SPEC_TYPE").toString()) ? "小区语音业务指标" : "小区数据业务指标"));

            stsConfig.setStsAnaItemDetail(stsAnaItemDetail);
            planConfigLists.add(stsConfig);
        }
        log.debug("返回：{}", planConfigLists);
        return planConfigLists;
    }

    /**
     * 统计资源利用率
     *
     * @param stsCode      点击的按钮的操作识别码
     * @param startIndex   开始序号
     * @param selectedList 列表的勾选结果集
     * @return GsmStsQueryResultDTO
     */
    @PostMapping("/statics-resource-utilization-rate")
    public GsmStsQueryResultDTO staticsResourceUtilizationRate(String stsCode, int startIndex, String selectedList) {
        log.debug("参数：stsCode={},startIndex={},selectedList={}", stsCode, startIndex, selectedList);
        String[] selectedStringList = selectedList.split(",");
        List<Integer> selectLists = new ArrayList<>();
        for (String one : selectedStringList) {
            selectLists.add(Integer.parseInt(one));
        }
        // 一次最多传送1000
        int size = 1000;
        List<GsmStsResultDTO> stsResults = gsmTrafficStaticsService.staticsResourceUtilizationRateInSelectList(stsCode, selectLists);

        log.debug("获取到stsCode=" + stsCode + "对应的统计数据：" + (stsResults == null ? stsResults : stsResults.size()));
        boolean hasMore = false;
        int toIndex = startIndex + size;
        int totalCnt = 0;

        GsmStsQueryResultDTO queryResult = new GsmStsQueryResultDTO();
        if (stsResults != null) {
            List<GsmStsResultDTO> subList = null;
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

        log.debug("返回：{}", queryResult);
        return queryResult;
    }

    /**
     * 统计符合某种要求的小区
     * @param stsCode      点击的按钮的操作识别码
     * @param startIndex   开始序号
     * @param selectedList 列表的勾选结果集
     * @author xiao.sz
     */
    @PostMapping("/statics-special-cell")
    public GsmStsQueryResultDTO staticsSpecialCell(String stsCode, int startIndex, String selectedList) {
        log.debug("参数：stsCode={},startIndex={},selectedList={}", stsCode, startIndex, selectedList);

        String[] selectedStringList = selectedList.split(",");
        List<Integer> selectLists = new ArrayList<>();
        for (String one : selectedStringList) {
            selectLists.add(Integer.parseInt(one));
        }
        // 一次最多传送1000
        int size = 1000;
        List<GsmStsResultDTO> stsResults = gsmTrafficStaticsService.staticsSpecialCellInSelectList(stsCode, selectLists);

        log.debug("获取到cellType={},对应的统计数据={},大小={}", stsCode, stsResults, stsResults.size());
        boolean hasMore = false;
        int toIndex = startIndex + size;
        int totalCnt = 0;

        GsmStsQueryResultDTO queryResult = new GsmStsQueryResultDTO();
        if (stsResults.size() > 0) {
            List<GsmStsResultDTO> subList = null;
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
        return queryResult;
    }
}
