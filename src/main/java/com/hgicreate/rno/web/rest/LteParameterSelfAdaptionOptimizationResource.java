package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.service.LteParameterSelfAdaptionOptimizationService;
import com.hgicreate.rno.service.dto.LteParameterSelfAdaptionOptimizationDTO;
import com.hgicreate.rno.web.rest.vm.LteParameterSelfAdaptionOptimizationVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lte-parameter-self-adaption-optimization")
public class LteParameterSelfAdaptionOptimizationResource {

    private final LteParameterSelfAdaptionOptimizationService lteParameterSelfAdaptionOptimizationService;

    public LteParameterSelfAdaptionOptimizationResource(LteParameterSelfAdaptionOptimizationService lteParameterSelfAdaptionOptimizationService) {
        this.lteParameterSelfAdaptionOptimizationService = lteParameterSelfAdaptionOptimizationService;
    }

    /**
     * 参数自适应调整查询入口
     *
     * @param vm
     * @return
     */
    @PostMapping("/query-cell-info")
    public List<LteParameterSelfAdaptionOptimizationDTO> queryImport(LteParameterSelfAdaptionOptimizationVM vm) {
        if ("440103".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州荔湾区", 40000, vm.getCellName());
        }
        if ("440104".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州越秀区", 43000, vm.getCellName());
        }
        if ("440105".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州海珠区", 47000, vm.getCellName());
        }
        if ("440106".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州天河区", 50000, vm.getCellName());
        }
        if ("440111".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州白云区", 53000, vm.getCellName());
        }
        if ("440112".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州黄埔区", 56000, vm.getCellName());
        }
        if ("440113".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州番禺区", 59000, vm.getCellName());
        }
        if ("440114".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州花都区", 62000, vm.getCellName());
        }
        if ("440115".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州南沙区", 65000, vm.getCellName());
        }
        if ("440117".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州从化区", 68000, vm.getCellName());
        }
        if ("440118".equals(vm.getDistrict())) {
            return lteParameterSelfAdaptionOptimizationService.queryAllTargetCells("广州增城区", 71000, vm.getCellName());
        }
        return null;
    }
}
