package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.service.LteCapicityOptimizationService;
import com.hgicreate.rno.service.dto.LteCapicityOptimizationDTO;
import com.hgicreate.rno.web.rest.vm.LteCapicityOptimizationVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lte-capicity-optimization")
public class LteCapicityOptimizationResource {

    private final LteCapicityOptimizationService lteCapicityOptimizationService;

    public LteCapicityOptimizationResource(LteCapicityOptimizationService lteCapicityOptimizationService) {
        this.lteCapicityOptimizationService = lteCapicityOptimizationService;
    }


    @PostMapping("/query-info")
    public List<LteCapicityOptimizationDTO> queryImport(LteCapicityOptimizationVM vm) throws ParseException {

        if("440103".equals(vm.getDistrict()) ){
                return lteCapicityOptimizationService.queryAllCells("广州荔湾区",40000);
        }
        if("440104".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州越秀区",43000);
        }
        if("440105".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州海珠区",47000);
        }
        if("440106".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州天河区",50000);
        }
        if("440111".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州白云区",53000);
        }
        if("440112".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州黄埔区",56000);
        }
        if("440113".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州番禺区",59000);
        }
        if("440114".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州花都区",62000);
        }
        if("440115".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州南沙区",65000);
        }
        if("440117".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州从化区",68000);
        }
        if("440118".equals(vm.getDistrict()) ){
            return lteCapicityOptimizationService.queryAllCells("广州增城区",71000);
        }
        return null;
    }
}
