package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.RnoPlanDesignService;
import com.hgicreate.rno.web.rest.gsm.vm.GsmPageVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmPlanConfigVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/gsm-frequency-reuse-analysis")
public class GsmFrequencyReuseAnalysisResource {

    @Autowired
    private RnoPlanDesignService rnoPlanDesignService;

    @GetMapping("/cell-config-analysis-list")
    public List<GsmPlanConfigVM> getCellConfigAnalysisList() {
        log.info("进入方法：getCellConfigAnalysisListForAjaxAction。");
        List<GsmPlanConfigVM> planConfigs = new ArrayList<GsmPlanConfigVM>();
        GsmPlanConfigVM p = new GsmPlanConfigVM();
        p.setCollectTime("2017/12/24");
        p.setTitle("GSM900小区");
        p.setConfigId("9760-4043");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setBtsType("GSM900");
        p.setName("系统配置");
        planConfigs.add(p);
        p = new GsmPlanConfigVM();
        p.setCollectTime("2017/12/24");
        p.setTitle("GSM1800小区");
        p.setConfigId("9760-4042");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setBtsType("GSM1800");
        p.setName("系统配置");
        planConfigs.add(p);

        log.info("退出方法：getCellConfigAnalysisListForAjaxAction。输出：" + planConfigs);
        return planConfigs;
    }

    /**
     * @return Map
     * @description: 统计指定区域范围小区的频率复用情况
     */
    @PostMapping("/statistics-frequency-reuse-info")
    public Map<String, Object> staticsFreqReuseInfo(String btsType, int currentPage, int pageSize, long areaId) {
        log.info("进入方法：staticsFreqReuseInfoForAjaxAction.btsType=" + btsType);
        Map<Integer, Object> freqReuseInfos;
        GsmPageVM page = new GsmPageVM(0, pageSize, currentPage, 0, 0);

        freqReuseInfos = this.rnoPlanDesignService.staticsFreqReuseInfoInArea(btsType, currentPage, pageSize, areaId);// 获取统计信息
        if (freqReuseInfos == null) {
            freqReuseInfos = Collections.EMPTY_MAP;
        }
        log.info("查询结果集为 = {}", freqReuseInfos);
        log.info("查询结果集大小为 = {}", freqReuseInfos.size());
        GsmPageVM newPage = new GsmPageVM();// 当前分页
        newPage.setCurrentPage(page.getCurrentPage());
        newPage.setPageSize(page.getPageSize());
        newPage.setTotalCnt(freqReuseInfos.size());
        newPage.setTotalPageCnt(freqReuseInfos.size() / newPage.getPageSize()
                + (freqReuseInfos.size() % newPage.getPageSize() == 0 ? 0 : 1));
        Map<Integer, Object> rMap = null;
        if (page != null) {// 获取分页统计
            int start = (page.getCurrentPage() - 1) * page.getPageSize();
            if (start < 0) {
                start = 0;
            }
            if (start >= 0) {
                int toIndex = start + page.getPageSize() - 1;
                System.out.println("start" + start);
                System.out.println("toIndex" + toIndex);
                if (toIndex > freqReuseInfos.size()) {
                    toIndex = freqReuseInfos.size();
                }
                int i = 0;
                rMap = new TreeMap<Integer, Object>();
                for (Integer key : freqReuseInfos.keySet()) {
                    if (i >= start && i <= toIndex) {
                        rMap.put(key, freqReuseInfos.get(key));
                    }
                    if (i == toIndex) {
                        break;
                    }
                    i++;
                }
            } else {
                rMap = freqReuseInfos;
            }
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("freqReuseInfos", rMap);
        resultMap.put("page", newPage);

        log.info("退出方法：staticsFreqReuseInfoForAjaxAction。输出：{}" + resultMap);
        return resultMap;
    }
}
