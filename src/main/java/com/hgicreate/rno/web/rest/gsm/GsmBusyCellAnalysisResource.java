package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmBusyCellAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author tao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-busy-cell-analysis")
public class GsmBusyCellAnalysisResource {
    private final GsmBusyCellAnalysisService gsmBusyCellAnalysisService;

    public GsmBusyCellAnalysisResource(GsmBusyCellAnalysisService gsmBusyCellAnalysisService) {
        this.gsmBusyCellAnalysisService = gsmBusyCellAnalysisService;
    }

    /**
     * 根据区域查找繁忙小区
     * @param areaId 区域id
     * @return 繁忙小区列表
     */
    @GetMapping("/busy-cell")
    public List<Map<String, Object>> getBusyCell(Long areaId){
        log.debug("areaId={}",areaId);
        return gsmBusyCellAnalysisService.getBusyCell(areaId);
    }

    /**
     * 根据主小区id查找闲邻区
     * @param cellId 主小区id
     * @param areaId 区域id
     * @return 闲邻区列表
     */
    @GetMapping("idle-ncell-detail")
    public List<Map<String,Object>> getIdleNcell(String cellId,Long areaId){
        return gsmBusyCellAnalysisService.getIdleNcell(cellId,areaId);
    }
}
