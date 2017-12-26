package com.hgicreate.rno.service.gsm;

import java.util.Map;

public interface RnoPlanDesignService {

    /**
     * @param btsType
     * @return Map<Integer,Object>
     * @description: 统计指定区域范围小区的频率复用情况
     * @author：yuan.yw
     * @date：Nov 7, 2013 11:59:55 AM
     */
    public Map<Integer, Object> staticsFreqReuseInfoInArea(String btsType, int currentPage, int pageSize, long areaId);
}
