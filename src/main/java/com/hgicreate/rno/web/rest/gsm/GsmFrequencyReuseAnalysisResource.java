package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmFrequencyReuseService;
import com.hgicreate.rno.web.rest.gsm.vm.GsmPageVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author xiao.sz
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-frequency-reuse-analysis")
public class GsmFrequencyReuseAnalysisResource {

    @Autowired
    private GsmFrequencyReuseService gsmFrequencyReuseService;

    /**
     * @return 页面配置列表
     */
    @GetMapping("/cell-config-analysis-list")
    public List<Map<String, Object>> getCellConfigAnalysisList() {
        List<Map<String, Object>> planConfigs = gsmFrequencyReuseService.getCellConfigAnalysisList();
        log.debug("返回：" + planConfigs);
        return planConfigs;
    }

    /**
     * 统计指定区域范围小区的频率复用情况
     * @param btsType 频率类型
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @param areaId 区域id
     * @return Map 结果集
     */
    @PostMapping("/statistics-frequency-reuse-info")
    public Map<String, Object> staticsFreqReuseInfo(String btsType, int currentPage, int pageSize, long areaId) {
        Map<Integer, Object> freqReuseInfos;
        GsmPageVM page = new GsmPageVM(0, pageSize, currentPage, 0, 0);
        // 获取统计信息
        freqReuseInfos = gsmFrequencyReuseService.staticsFreqReuseInfoInArea(btsType, currentPage, pageSize, areaId);
        if (freqReuseInfos == null) {
            freqReuseInfos = Collections.EMPTY_MAP;
        }
        // 当前分页
        GsmPageVM newPage = new GsmPageVM();
        newPage.setCurrentPage(page.getCurrentPage());
        newPage.setPageSize(page.getPageSize());
        newPage.setTotalCnt(freqReuseInfos.size());
        newPage.setTotalPageCnt(freqReuseInfos.size() / newPage.getPageSize()
                + (freqReuseInfos.size() % newPage.getPageSize() == 0 ? 0 : 1));
        Map<Integer, Object> rMap = null;
        // 获取分页统计
        int start = (page.getCurrentPage() - 1) * page.getPageSize();
        if (start < 0) {
            start = 0;
        }
        if (start >= 0) {
            int toIndex = start + page.getPageSize() - 1;
            if (toIndex > freqReuseInfos.size()) {
                toIndex = freqReuseInfos.size();
            }
            int i = 0;
            rMap = new TreeMap<>();
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
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("freqReuseInfos", rMap);
        resultMap.put("page", newPage);

        log.debug("返回：{}" + resultMap);
        return resultMap;
    }
}
