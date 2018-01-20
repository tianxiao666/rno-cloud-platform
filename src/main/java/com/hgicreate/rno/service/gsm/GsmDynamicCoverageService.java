package com.hgicreate.rno.service.gsm;

import java.util.List;
import java.util.Map;

public interface GsmDynamicCoverageService {

    /**
     * 获取画小区动态覆盖图所需的数据
     */
    Map<String, List<Map<String, Object>>> getDynaCoverageDataByCityAndDate(
            String enName, String cellId, long cityId, String startDate, String endDate);

    /**
     * 获取cell的邻区
     * @param cellId
     * @param cityId
     * @return
     */
    List<Map<String, Object>> getNcellDetailsByCellAndAreaId(String cellId, long cityId);
}
