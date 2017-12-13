package com.hgicreate.rno.web.rest.gsm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hgicreate.rno.domain.gsm.AreaRectangle;
import com.hgicreate.rno.service.gsm.RnoLteGisService;
import com.hgicreate.rno.service.gsm.RnoNcsDynaCoverageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/dynamicCoverageMapPage")
public class GsmDynamicCoverageResource {
    private static Gson gson = new GsonBuilder().create();// 线程安全
    @Autowired
    private final RnoLteGisService rnoLteGisService;
    @Autowired
    private final RnoNcsDynaCoverageService rnoNcsDynaCoverageService;

    @Autowired
    public GsmDynamicCoverageResource(RnoLteGisService rnoLteGisService, RnoNcsDynaCoverageService rnoNcsDynaCoverageService) {
        this.rnoLteGisService = rnoLteGisService;
        this.rnoNcsDynaCoverageService = rnoNcsDynaCoverageService;
    }

    @RequestMapping("/getDynaCoverageDataForAction")
    public String getDynaCoverageDataForAction(String cellId, long cityId, String startDate, String endDate) {
        log.debug("获取画小区动态覆盖图(曲线)所需的数据, cell=" + cellId + ", cityId=" + cityId
                + ", startDate=" + startDate + ", endDate=" + endDate);

        // 基准点
        Map<AreaRectangle, List<Map<String, Object>>> standardPoints = null;
        standardPoints = rnoLteGisService
                .getSpecialAreaRnoMapLnglatRelaGpsMapList(cityId,
                        "BAIDU");
        log.debug("standardPoints == {}", standardPoints);
        Map<String, List<Map<String, Object>>> res = rnoNcsDynaCoverageService
                .getDynaCoverageDataByCityAndDate(cellId, cityId, startDate, endDate, standardPoints);
        String result = gson.toJson(res);
        log.debug("result == {}", result);
        return result;
    }

    @RequestMapping("/getDynaCoverageData2ForAction")
    public String getDynaCoverageData2ForAction(String cellId, long cityId, String startDate, String endDate, double imgCoeff) {
        log.debug("获取画小区动态覆盖图(曲线)所需的数据, cellId=" + cellId + ", cityId=" + cityId
                + ", startDate=" + startDate + ", endDate=" + endDate + ", imgCoeff=" + imgCoeff);

        // 基准点
        Map<AreaRectangle, List<Map<String, Object>>> standardPoints = null;
        standardPoints = rnoLteGisService
                .getSpecialAreaRnoMapLnglatRelaGpsMapList(cityId,
                        "BAIDU");
        log.debug("standardPoints == {}", standardPoints);
        Map<String, List<Map<String, Object>>> res = rnoNcsDynaCoverageService
                .getDynaCoverageData2ByCityAndDate(cellId, cityId, startDate, endDate, imgCoeff, standardPoints);
        String result = gson.toJson(res);
        log.debug("result == {}", result);
        return result;
    }

    /**
     * 从MR数据表中获取干扰数据
     */
    @RequestMapping("/getLteDynamicCoverageShapeData")
    public List<Map<String, String>> getLteDynamicCoverageShapeData(String lteCellId, long cityId, String dates, String inOrOut) {
        log.debug("获取画LTE小区动态覆盖图(折线)所需的数据, lteCellId={}, cityId={}, dates={} inOrOut={}", lteCellId, cityId, dates, inOrOut);
        List<Map<String, String>> res = rnoLteGisService.getLteDynamicCoverageShapeData(lteCellId, cityId, dates, inOrOut);
        log.debug("获取画LTE小区动态覆盖图(折线)所需的数据, res.size={}", res.size());
        log.debug("获取画LTE小区动态覆盖图(折线)所需的数据, res={}", res.get(0));
        log.debug("获取画LTE小区动态覆盖图(折线)所需的数据, res={}", res.get(1));
        return res;
    }

    /* PCI评估 */

    /**
     * in干扰数据
     */
    @RequestMapping("/in")
    public List<Map<String, Object>> in(int jobId, String cellId) {
        log.debug("jobId={},cellId={}", jobId, cellId);
        return rnoLteGisService.getInCellInfo(jobId, cellId);
    }

    /**
     * out干扰数据
     */
    @RequestMapping("/out")
    public List<Map<String, Object>> out(int jobId, String ncellId) {
        log.debug("jobId={},ncellId={}", jobId, ncellId);
        return rnoLteGisService.getOutCellInfo(jobId, ncellId);
    }

    /**
     * pci干扰数据
     */
    @RequestMapping("/pci")
    public List<Map<String, Object>> pci(int jobId, String cellId) {
        log.debug("jobId={},cellId={}", jobId, cellId);
        List<Map<String, Object>> pciInfo = rnoLteGisService.getPciCellInfo(jobId, cellId);
        log.debug("pciInfo={}", pciInfo.size());
        return pciInfo;
    }

    /**
     * 转换lte小区与某同站小区的pci
     */
    @RequestMapping("/changeLteCellPciForAjaxAction")
    public Map<String, Boolean> changeLteCellPciForAjaxAction(String cell1, String cell2, String pci1, String pci2) {
        log.debug("changeLteCellPciForAjaxAction. cell1={}, pci1={}, cell2={}, pci2={}", cell1, pci1, cell2, pci2);
        return rnoLteGisService.changeLteCellPci(cell1, pci1, cell2, pci2);
    }
}
