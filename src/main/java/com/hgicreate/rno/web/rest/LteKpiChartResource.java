package com.hgicreate.rno.web.rest;


import com.hgicreate.rno.service.LteKpiChartService;
import com.hgicreate.rno.web.rest.vm.LteKpiChartVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/lte-kpi-chart")
public class LteKpiChartResource {

    private final LteKpiChartService lteKpiChartService;

    public LteKpiChartResource(LteKpiChartService lteKpiChartService) {
        this.lteKpiChartService = lteKpiChartService;
    }

    @GetMapping("/chart-query")
    public ResponseEntity<?> queryChartData(LteKpiChartVM lteKpiChartVM) throws ParseException {
        log.debug("视图模型vm ={}", lteKpiChartVM);
       return lteKpiChartService.queryChartData(lteKpiChartVM);
    }
}
