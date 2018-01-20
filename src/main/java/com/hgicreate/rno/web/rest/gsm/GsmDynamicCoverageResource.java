package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmDynamicCoverageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author xiao.sz
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/gsm-dynamic-coverage")
public class GsmDynamicCoverageResource {
    @Autowired
    private GsmDynamicCoverageService rnoNcsDynaCoverageService;

    /**
     * 根据参数获得在日期区间内的覆盖图
     * @param enName 小区英文名
     * @param cityId 城市id
     * @param cellId 小区id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return res 结果集
     */
    @GetMapping("/dynamic-coverage-data")
    public Map<String, List<Map<String, Object>>> getDynamicCoverageData(String enName, long cityId, String cellId, String startDate, String endDate) {
        Map<String, List<Map<String, Object>>> res = rnoNcsDynaCoverageService
                .getDynaCoverageDataByCityAndDate(enName, cellId, cityId, startDate, endDate);
        log.debug("result == {}", res);
        return res;
    }

    /**
     * 根据输入的主小区id 和当前的城市id 获取该小区的邻区
     * @param cellId 主小区id
     * @param cityId 城市id
     * @return res 邻区结果集
     */
    @GetMapping("/ncell-details")
    public List<Map<String, Object>> getNcellDetails(String cellId, long cityId) {
        List<Map<String, Object>> res = rnoNcsDynaCoverageService.getNcellDetailsByCellAndAreaId(cellId, cityId);
        log.debug("邻区 == {}" + res);
        return res;
    }
}
