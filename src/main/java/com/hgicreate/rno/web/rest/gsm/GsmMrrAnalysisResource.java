package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmMrrAnalysisService;
import com.hgicreate.rno.web.rest.gsm.vm.GsmParamCheckVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gsm-mrr-analysis")
public class GsmMrrAnalysisResource {

    private final GsmMrrAnalysisService gsmMrrAnalysisService;

    public GsmMrrAnalysisResource(GsmMrrAnalysisService gsmMrrAnalysisService) {
        this.gsmMrrAnalysisService = gsmMrrAnalysisService;
    }

    @GetMapping("/query-mrr-data")
    public List<Map<String, Object>> queryParam(GsmParamCheckVM vm) {
        log.debug("进入GSM MRR指标分析数据查询方法,视图模型={}",vm);
        return null;
    }

    @GetMapping("/get-bsc-by-cityId")
    public Map<String, List<Map<String, Object>>> queryReport(String cityId) {
        log.debug("查询bsc的区域id为：{}", cityId);
        return gsmMrrAnalysisService.queryAllBscByAreaId(cityId);
    }

}
