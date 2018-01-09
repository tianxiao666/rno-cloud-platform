package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.repository.gsm.GsmBscDataRepository;
import com.hgicreate.rno.service.gsm.GsmParamCheckService;
import com.hgicreate.rno.service.gsm.dto.GsmBscDataDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmBscDataMessageMapper;
import com.hgicreate.rno.util.ExcelFileTool;
import com.hgicreate.rno.web.rest.gsm.vm.GsmParamCheckVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zeng.dh1
 * @date 2017/12/15
 */

@Slf4j
@RestController
@RequestMapping("/api/gsm-param-check")
public class GsmParamCheckResource {

    private final GsmParamCheckService gsmParamCheckService;
    private final GsmBscDataRepository gsmBscDataRepository;

    public GsmParamCheckResource(GsmParamCheckService gsmParamCheckService,
                                 GsmBscDataRepository gsmBscDataRepository) {
        this.gsmParamCheckService = gsmParamCheckService;
        this.gsmBscDataRepository = gsmBscDataRepository;
    }

    @GetMapping("/check-param")
    public List<Map<String, Object>> queryParam(GsmParamCheckVM vm) {
        log.debug("进入GSM一致性数据检查方法,视图模型={}",vm);
        return gsmParamCheckService.checkParamData(vm);
    }

    @PostMapping("/export-param-check-data")
    public void exportParamData(GsmParamCheckVM vm, HttpServletResponse resp) {
        log.debug("进入一致性数据导出方法,视图模型={}",vm);
        //设置标题
        String fileName = "GSM一致性检查.xlsx";
        try {
            fileName = new String("GSM一致性检查.xlsx".getBytes("UTF-8"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/x.ms-excel");
        resp.setHeader("Content-disposition", "attachment;filename=" + fileName);
        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
        for (String powerCheck : vm.getItems().split(",")) {
            vm.setCheckType(powerCheck);
            //获取sheetname与对应的list
            String sheetName = "";
            if (("powerCheck").equals(vm.getCheckType())) {
                sheetName = "功率检查";
            } else if (("freqHopCheck").equals(vm.getCheckType())) {
                sheetName = "跳频检查";
            } else if (("nccperm").equals(vm.getCheckType())) {
                sheetName = "NCCPERM检查";
            } else if (("meaFreqMultidefined").equals(vm.getCheckType())) {
                sheetName = "测量频点多定义";
            } else if (("meaFreqMomit").equals(vm.getCheckType())) {
                sheetName = "测量频点漏定义";
            } else if (("baNumCheck").equals(vm.getCheckType())) {
                sheetName = "BA表个数检查";
            } else if (("talimMaxTa").equals(vm.getCheckType())) {
                sheetName = "TALIM_MAXTA检查";
            } else if (("sameFreqBsicCheck").equals(vm.getCheckType())) {
                sheetName = "同频同bsic检查";
            } else if (("ncellNumCheck").equals(vm.getCheckType())) {
                sheetName = "邻区过多过少检查";
            } else if (("ncellMomit").equals(vm.getCheckType())) {
                sheetName = "本站邻区漏定义";
            } else if (("unidirNcell").equals(vm.getCheckType())) {
                sheetName = "单向邻区检查";
            } else if (("sameNcellFreqCheck").equals(vm.getCheckType())) {
                sheetName = "同邻频检查";
            }
            map.put(sheetName, gsmParamCheckService.checkParamData(vm));
        }
        //把map放进工具导出
        ExcelFileTool.createExcel(resp, map);
    }

    @GetMapping("/check-bsc-by-cityId")
    public List<GsmBscDataDTO> queryReport(String cityId) {
        log.debug("查询bsc的区域id为：{}", cityId);
        Area area = new Area();
        area.setId(Long.parseLong(cityId));
        return gsmBscDataRepository.findByAreaAndStatus(area, "N")
                .stream().map(GsmBscDataMessageMapper.INSTANCE::bscDataToBscDataDto)
                .collect(Collectors.toList());
    }

}
