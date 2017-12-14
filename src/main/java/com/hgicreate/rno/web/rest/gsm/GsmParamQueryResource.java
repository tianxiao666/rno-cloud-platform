package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.mapper.gsm.GsmParamQueryMapper;
import com.hgicreate.rno.service.gsm.GsmParamCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gsm-param-query")
public class GsmParamQueryResource {

    private final GsmParamCheckService gsmParamCheckService;
    private final GsmParamQueryMapper gsmParamQueryMapper;

    public GsmParamQueryResource(GsmParamCheckService gsmParamCheckService,
                                 GsmParamQueryMapper gsmParamQueryMapper) {
        this.gsmParamCheckService = gsmParamCheckService;
        this.gsmParamQueryMapper = gsmParamQueryMapper;
    }

    /*@GetMapping("/check-param")
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
    }*/

    @GetMapping("/query-param-by-cityId")
    public Map<String,List<Map<String, Object>>> queryReport(String cityId,String dataType) {
        log.debug("查询参数的id为：{}", cityId);
        int areaId = Integer.parseInt(cityId);
        List<Map<String, Object>> bscList = gsmParamQueryMapper.queryBscListByCityId(areaId);
        List<Map<String, Object>> dateList = gsmParamQueryMapper.queryDateListByCityId(areaId,dataType);
        Map<String,List<Map<String, Object>>> res = new HashMap<String,List<Map<String, Object>>>();
        res.put("bscInfo",bscList);
        res.put("dateInfo",dateList);
        return res;
    }

}
