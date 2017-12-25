package com.hgicreate.rno.web.rest.gsm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hgicreate.rno.service.gsm.RnoPlanDesignService;
import com.hgicreate.rno.web.rest.gsm.vm.GsmGisCellQueryResultVM;
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
    private static Gson gson = new GsonBuilder().create();// 线程安全

    @Autowired
    private RnoPlanDesignService rnoPlanDesignService;


    @GetMapping("/cell-config-analysis-list")
    public String getCellConfigAnalysisList() {
        log.info("进入方法：getCellConfigAnalysisListForAjaxAction。");
//        List<GsmPlanConfigVM> planConfigs = null;
//        planConfigs = (List<GsmPlanConfigVM>) SessionService.getInstance().getValueByKey("PLAN_LOAD_CELL_CONFIG_ID");

        List<GsmPlanConfigVM> planConfigs = new ArrayList<GsmPlanConfigVM>(); //测试hardcode
        GsmPlanConfigVM p = new GsmPlanConfigVM();
        p.setCollectTime("2013/12/24");
        p.setTitle("官湖M3");
        p.setConfigId("9760-4043");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setName("系统配置");
        planConfigs.add(p);
        p = new GsmPlanConfigVM();
        p.setCollectTime("2013/12/24");
        p.setTitle("官湖M3");
        p.setConfigId("9760-4043");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setName("系统配置");
        planConfigs.add(p);

        if (planConfigs == null) {
            planConfigs = Collections.EMPTY_LIST;
        }
        String result = gson.toJson(planConfigs);
        log.info("退出方法：getCellConfigAnalysisListForAjaxAction。输出：" + result);
        return result;
    }

    /**
     * @return void
     * @description: 获取小区干扰加载的分析列表
     * @author：yuan.yw
     * @date：Nov 6, 2013 5:23:13 PM
     */
    @GetMapping("/cell-interference-analysis-list")
    public String getCellInterferenceAnalysisList() {
        log.info("进入方法：getCellInterferenceAnalysisListForAjaxAction。");
        List<GsmPlanConfigVM> planConfigs = new ArrayList<GsmPlanConfigVM>(); //测试hardcode
        GsmPlanConfigVM p = new GsmPlanConfigVM();
        p.setCollectTime("2013/12/24");
        p.setTitle("官湖M3");
        p.setConfigId("9760-4043");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setName("系统配置");
        planConfigs.add(p);
        p = new GsmPlanConfigVM();
        p.setCollectTime("2013/12/24");
        p.setTitle("官湖M3");
        p.setConfigId("9760-4043");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setName("系统配置");
        planConfigs.add(p);

        if (planConfigs == null) {
            planConfigs = Collections.EMPTY_LIST;
        }
        String result = gson.toJson(planConfigs);
        log.info("退出方法：getCellInterferenceAnalysisListForAjaxAction。输出：" + result);
        return result;
    }

    /**
     * @return void
     * @description: 根据类型获取选择的小区配置或小区干扰中分析列表中的小区的gis信息
     * @author：yuan.yw
     * @date：Nov 8, 2013 9:59:47 AM
     */
    @PostMapping("/frequency-reuse-cell-gis-info")
    public String getFreqReuseCellGisInfoFromSelAnaListForAjaxAction(String configId) {
        log.info("进入getFreqReuseCellGisInfoFromSelAnaListForAjaxAction。configId=" + configId);
        List<GsmPlanConfigVM> planConfigs = null;
        GsmPlanConfigVM selectConfig = null;
        GsmPageVM page = new GsmPageVM();
        //测试hard code
        GsmPlanConfigVM p = new GsmPlanConfigVM();
        p.setCollectTime("2013/12/24");
        p.setTitle("官湖M3");
        p.setConfigId("9760-4043");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setName("系统配置");
        selectConfig = p;

        GsmGisCellQueryResultVM gisCellResults = null;
        if (selectConfig == null) {
            gisCellResults = new GsmGisCellQueryResultVM();
            gisCellResults.setTotalCnt(0);
        } else {
            page.setForcedStartIndex(-1);
            gisCellResults = this.rnoPlanDesignService.getFreqReuseCellGisInfoFromSelectionList(selectConfig, page);
        }
        int totalCnt = gisCellResults.getTotalCnt();

        GsmPageVM newPage = new GsmPageVM();
        newPage.setCurrentPage(page.getCurrentPage());
        newPage.setPageSize(page.getPageSize());
        newPage.setTotalCnt(totalCnt);
        newPage.setTotalPageCnt(totalCnt / newPage.getPageSize() + (totalCnt % newPage.getPageSize() == 0 ? 0 : 1));
        newPage.setForcedStartIndex(-1);
        gisCellResults.setPage(newPage);
        String result = gson.toJson(gisCellResults);
        log.info("退出getFreqReuseCellGisInfoFromSelAnaListForAjaxAction。");
        return result;
    }

    /**
     * @return void
     * @description: 统计指定区域范围小区的频率复用情况
     * @author：yuan.yw
     * @date：Nov 7, 2013 11:51:19 AM
     */
    @PostMapping("/statistics-frequency-reuse-info")
    public String staticsFreqReuseInfo(String configId, int currentPage, int pageSize) {
        log.info("进入方法：staticsFreqReuseInfoForAjaxAction.configId=" + configId);
        Map<Integer, Object> freqReuseInfos = null;
        List<GsmPlanConfigVM> planConfigs = null;
        GsmPageVM page = new GsmPageVM(0, pageSize, currentPage, 0, 0);
        GsmPlanConfigVM selectConfig = null;
//        if (planConfigs != null && planConfigs.size() > 0) {
//            for (int i = 0; i < planConfigs.size(); i++) {
//                if (planConfigs.get(i).getConfigId() == configId) {
//                    selectConfig = planConfigs.get(i);// 选中分析列表
//                    log.info("selectConfig.isTemp():" + selectConfig.isTemp());
//                }
//            }
//        }

        //测试hard code
        GsmPlanConfigVM p = new GsmPlanConfigVM();
        p.setCollectTime("2013/12/24");
        p.setTitle("官湖M3");
        p.setConfigId("9760-4043");
        p.setSelected(false);
        p.setTemp(false);
        p.setType("CELLDATA");
        p.setName("系统配置");
        selectConfig = p;

        freqReuseInfos = this.rnoPlanDesignService.staticsFreqReuseInfoInArea(selectConfig);// 获取统计信息
        if (freqReuseInfos == null) {
            freqReuseInfos = Collections.EMPTY_MAP;
        }
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
        Gson gson = new GsonBuilder().create();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("freqReuseInfos", rMap);
        resultMap.put("page", newPage);
        String result = gson.toJson(resultMap);
        log.info("退出方法：staticsFreqReuseInfoForAjaxAction。输出：" + result);
        return result;

    }
}
