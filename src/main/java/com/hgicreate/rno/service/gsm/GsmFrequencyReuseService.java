package com.hgicreate.rno.service.gsm;

import java.util.List;
import java.util.Map;

/**
 * @author xiao.sz
 */
public interface GsmFrequencyReuseService {

    /**
     * show 统计指定区域范围小区的频率复用情况
     * @param btsType 频率类型
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @param areaId 区域id
     * @return Map<Integer,Object>
     *
     */
    Map<Integer, Object> staticsFreqReuseInfoInArea(String btsType, int currentPage, int pageSize, long areaId);

    /**
     * show 获取配置列表
     * @return 结果集
     */
    List<Map<String, Object>> getCellConfigAnalysisList();
}
