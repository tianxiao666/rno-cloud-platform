package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmGisCellQueryResultVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmPageVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmPlanConfigVM;

import java.util.Map;


public interface RnoPlanDesignService {
    /**
     * @param selectConfig
     * @param page
     * @return GisCellQueryResult
     * @description: 根据类型获取选择的小区配置或小区干扰中分析列表中的小区的gis信息
     * @author：yuan.yw
     * @date：Nov 8, 2013 10:08:37 AM
     */
    GsmGisCellQueryResultVM getFreqReuseCellGisInfoFromSelectionList(GsmPlanConfigVM selectConfig, GsmPageVM page);

    /**
     * @param selectConfig
     * @return Map<Integer       ,       Object>
     * @description: 统计指定区域范围小区的频率复用情况
     * @author：yuan.yw
     * @date：Nov 7, 2013 11:59:55 AM
     */
    public Map<Integer, Object> staticsFreqReuseInfoInArea(GsmPlanConfigVM selectConfig);
}
