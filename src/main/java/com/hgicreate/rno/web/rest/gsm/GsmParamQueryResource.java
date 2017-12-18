package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.mapper.gsm.GsmParamQueryMapper;
import com.hgicreate.rno.util.ExcelFileTool;
import com.hgicreate.rno.web.rest.gsm.vm.GsmParamQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gsm-param-query")
public class GsmParamQueryResource {

    private final GsmParamQueryMapper gsmParamQueryMapper;

    public GsmParamQueryResource(GsmParamQueryMapper gsmParamQueryMapper) {
        this.gsmParamQueryMapper = gsmParamQueryMapper;
    }

    @PostMapping("/query-cell-param")
    public List<Map<String, Object>> queryParam(GsmParamQueryVM vm) {
        log.debug("进入网络参数cell查询方法,视图模型={}", vm);
        //处理param返回结果为空的问题
        if(vm.getCellParam().indexOf(",")>-1) {
            String param = "";
            for(String p: vm.getCellParam().split(",")) {
                param += "nvl(" + p + ",0) as "+ p + ",";
            }
            vm.setCellParam(param.substring(0,param.length()-1));
        }else {
            vm.setCellParam("nvl("+vm.getCellParam()+",0) as "+ vm.getCellParam());
        }

        vm.setDateList(vm.getCellDate().split(","));
        vm.setBscList(vm.getCellBsc().split(","));
        vm.setCellList(vm.getCellForCell().split(","));

        if("cell".equals(vm.getType())) {
            return gsmParamQueryMapper.getCellParamRecord(vm);
        }else if("channel".equals(vm.getType())) {
            return gsmParamQueryMapper.getChannelParamRecord(vm);
        }else {
            return gsmParamQueryMapper.getNcellParamRecord(vm);
        }
    }


    @PostMapping("/export-param-query-data")
    public void exportParamData(GsmParamQueryVM vm, HttpServletResponse resp) {
        log.debug("进入网络参数核查导出方法,视图模型={}",vm);
        //设置标题
        String fileName = "网络参数检查.xlsx";
        try {
            fileName = new String("网络参数检查.xlsx".getBytes("UTF-8"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/x.ms-excel");
        resp.setHeader("Content-disposition", "attachment;filename=" + fileName);

        //处理param返回结果为空的问题
        if(vm.getCellParam().indexOf(",")>-1) {
            String param = "";
            for(String p: vm.getCellParam().split(",")) {
                param += "nvl(" + p + ",0) as "+ p + ",";
            }
            vm.setCellParam(param.substring(0,param.length()-1));
        }else {
            vm.setCellParam("nvl("+vm.getCellParam()+",0) as "+ vm.getCellParam());
        }

        vm.setDateList(vm.getCellDate().split(","));
        vm.setBscList(vm.getCellBsc().split(","));
        vm.setCellList(vm.getCellForCell().split(","));

        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
        String sheetName = "ParamQuery";
        if("cell".equals(vm.getType())) {
            map.put(sheetName,  gsmParamQueryMapper.getCellParamRecord(vm));
        }else if("channel".equals(vm.getType())) {
            map.put(sheetName,  gsmParamQueryMapper.getChannelParamRecord(vm));
        }else {
            map.put(sheetName,  gsmParamQueryMapper.getNcellParamRecord(vm));
        }

        //把map放进工具导出
        ExcelFileTool.createExcel(resp, map);
    }

    @GetMapping("/query-param-by-cityId")
    public Map<String, List<Map<String, Object>>> queryReport(String cityId, String dataType) {
        log.debug("查询参数的id为：{}", cityId);
        int areaId = Integer.parseInt(cityId);

        List<Map<String, Object>> bscList = gsmParamQueryMapper.queryBscListByCityId(areaId);
        List<Map<String, Object>> dateList = gsmParamQueryMapper.queryDateListByCityId(areaId, dataType);
        Map<String, List<Map<String, Object>>> res = new HashMap<String, List<Map<String, Object>>>();
        res.put("bscInfo", bscList);
        res.put("dateInfo", dateList);
        return res;
    }


}
