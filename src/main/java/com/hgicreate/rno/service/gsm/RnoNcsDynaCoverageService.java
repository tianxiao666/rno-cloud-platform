package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.gsm.AreaRectangle;

import java.util.List;
import java.util.Map;

public interface RnoNcsDynaCoverageService {

    /**
     * 获取画小区动态覆盖图所需的数据
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    Map<String, List<Map<String, Object>>> getDynaCoverageDataByCityAndDate(
            String cell, long cityId, String startDate, String endDate,
            Map<AreaRectangle, List<Map<String, Object>>> standardPoints);

    /**
     * 获取画小区动态覆盖图(折线)所需的数据
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年4月30日9:56:48
     * @company 怡创科技
     */
    Map<String, List<Map<String, Object>>> getDynaCoverageData2ByCityAndDate(
            String cell, long cityId, String startDate, String endDate,
            double imgCoeff,
            Map<AreaRectangle, List<Map<String, Object>>> standardPoints);

}
