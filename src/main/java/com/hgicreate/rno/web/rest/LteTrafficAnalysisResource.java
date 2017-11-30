package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.service.LteTrafficAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/lte-traffic-analysis")
public class LteTrafficAnalysisResource {

    private final LteTrafficAnalysisService lteTrafficAnalysisService;

    public LteTrafficAnalysisResource(LteTrafficAnalysisService lteTrafficAnalysisService) {
        this.lteTrafficAnalysisService = lteTrafficAnalysisService;
    }

    @GetMapping("/cell-record")
    public Map<String, Object> getCellRecord(String cellIds) {
        log.debug("cellIds={}", cellIds);
        return lteTrafficAnalysisService.getCellRecord(cellIds);
    }

    @GetMapping("/cell-index")
    public Map<String, Object> getCellIndex(String beginTime, String cellId) {
        log.debug("beginTime={}, cellId={}", beginTime, cellId);
        return lteTrafficAnalysisService.getCellIndex(beginTime, cellId);
    }

    @GetMapping("/problem-cell")
    public List<Map<String, Object>> getProblemCell(Long areaId) {
        log.debug("areaId={}", areaId);
        return lteTrafficAnalysisService.getProblemCell(areaId);
    }

    @GetMapping("/one-problem-cell")
    public Map<String, Object> getOneProblemCell(String cellId, String beginTime) {
        log.debug("cellId={}, beginTime={}", cellId, beginTime);
        return lteTrafficAnalysisService.getOneProblemCell(cellId, beginTime);
    }

}
