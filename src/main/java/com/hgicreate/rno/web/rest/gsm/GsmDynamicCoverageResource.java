package com.hgicreate.rno.web.rest.gsm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hgicreate.rno.service.gsm.RnoNcsDynaCoverageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/dynamicCoverageMapPage")
public class GsmDynamicCoverageResource {
    private static Gson gson = new GsonBuilder().create();// 线程安全
    @Autowired
    private RnoNcsDynaCoverageService rnoNcsDynaCoverageService;

    @RequestMapping("/getDynaCoverageDataForAction")
    public String getDynaCoverageDataForAction(String enName, long cityId, String cellId, String startDate, String endDate) {
        log.debug("获取画小区动态覆盖图(曲线)所需的数据, cell=" + enName + ", cityId=" + cityId
                + ", startDate=" + startDate + ", endDate=" + endDate);
        Map<String, List<Map<String, Object>>> res = rnoNcsDynaCoverageService
                .getDynaCoverageDataByCityAndDate(enName, cellId, cityId, startDate, endDate);
        String result = gson.toJson(res);
        log.debug("result == {}", result);
        return result;
    }

    @RequestMapping("/getNcellDetailsByCellandCityIdForAjaxAction")
    public String getNcellDetailsByCellAndAreaIdForAjaxAction(String cell, long cityId) {
        log.info("进入getNcellDetailsByCellandCityIdForAjaxAction方法,cell=" + cell + ",cityId=" + cityId);

        List<Map<String, Object>> res = rnoNcsDynaCoverageService.getNcellDetailsByCellAndAreaId(cell, cityId);
        log.debug("邻区 == {}" + res);
        String result = gson.toJson(res);
        return result;
    }
}
