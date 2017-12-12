package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmParamChangeService;
import com.hgicreate.rno.util.ExcelFileTool;
import com.hgicreate.rno.web.rest.gsm.vm.GsmParamChangeVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gsm-param-change")
public class GsmParamChangeResource {

    private final GsmParamChangeService gsmParamChangeService;

    public GsmParamChangeResource(GsmParamChangeService gsmParamChangeService) {
        this.gsmParamChangeService = gsmParamChangeService;
    }

    @GetMapping("/change-param")
    public List<Map<String, Object>> queryParam(GsmParamChangeVM vm) {
        log.debug("进入GSM参数变动核查查询参数方法,视图模型={}",vm);
        //爱立信小区参数对比结果集对象
        List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
        Map<String,Object> dateMessageMap = new LinkedHashMap<>();

        //判断第一个日期是否存在小区数据
        vm.setDate(vm.getDate1());
        Boolean flag1 = gsmParamChangeService.queryDateExist(vm);
        if(flag1) {
            //判断第二个日期是否存在小区数据
            vm.setDate(vm.getDate2());
            boolean flag2 = gsmParamChangeService.queryDateExist(vm);
            if(flag2) {
                //获取对比差异
                res = gsmParamChangeService.changeParamData(vm);
            } else {
                dateMessageMap.put( "dateMessage", "第二个日期不存在小区数据");
                res.add(dateMessageMap);
            }
        } else {
            dateMessageMap.put( "dateMessage", "第一个日期不存在小区数据");
            res.add(dateMessageMap);
        }
        return res;
    }

    @GetMapping("/get-param-detail")
    public List<Map<String, Object>> getParamDetail(GsmParamChangeVM vm) {
        log.debug("进入GSM参数变动核查查询详情方法,视图模型={}",vm);
        return gsmParamChangeService.queryParamDeatialData(vm);
    }

    @PostMapping("/export-param-change-data")
    public List<Map<String, Object>> exportParamData(GsmParamChangeVM vm, HttpServletResponse resp) {
        log.debug("进入GSM参数变动核查导出参数方法,视图模型={}",vm);
        List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> resF = new ArrayList<Map<String,Object>>();
        Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
        //设置标题
        String fileName = "GSM参数变动核查.xlsx";
        try {
            fileName = new String("GSM参数变动核查.xlsx".getBytes("UTF-8"), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/x.ms-excel");
        resp.setHeader("Content-disposition", "attachment;filename=" + fileName);
        //获取文件内容
        Map<String,Object> dateMessageMap = new LinkedHashMap<>();
        //判断第一个日期是否存在小区数据
        vm.setDate(vm.getDate1());
        Boolean flag1 = gsmParamChangeService.queryDateExist(vm);
        if(flag1) {
            //判断第二个日期是否存在小区数据
            vm.setDate(vm.getDate2());
            boolean flag2 = gsmParamChangeService.queryDateExist(vm);
            if(flag2) {
                //获取对比差异
                resF = gsmParamChangeService.exportChangeParamData(vm);
                if(resF.size() == 0) {
                    dateMessageMap.put( "dateMessage", "两个日期之间不存在差异参数");
                    res.add(dateMessageMap);
                }else {
                    map.put("gsm参数核查", resF);
                    //把map放进工具导出
                    ExcelFileTool.createExcel(resp, map);
                }
            } else {
                dateMessageMap.put( "dateMessage", "第二个日期不存在小区数据");
                res.add(dateMessageMap);
            }
        } else {
            dateMessageMap.put( "dateMessage", "第一个日期不存在小区数据");
            res.add(dateMessageMap);
        }
        return res;
    }

}
