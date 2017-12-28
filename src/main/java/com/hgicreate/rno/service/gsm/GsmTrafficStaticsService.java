package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmStsConfigVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStsResultVM;

import java.util.List;
import java.util.Map;

public interface GsmTrafficStaticsService {
    /**
     * 改造：通过小区语音和数据话统配置ID的字符串获取话统指标描述数据
     *
     * @param configIds
     * @return
     * @author chao.xj
     * @date 2013-12-10下午05:46:52
     */
    public List<Map<String, Object>> getCellAudioOrDataDescByConfigIds(final String configIds);

    public List<Map<String, Object>> getRnoTraffic(final String configIds);

    List<Map<String,Object>> getRnoTrafficRendererByTrafficCodeAndAreaId(String trafficCode, int areaId);

    List<Map<String,Object>> getDefaultRnoTrafficRenderer(String trafficCode);

    List<GsmStsResultVM> staticsResourceUtilizationRateInSelList(String stsCode, List<Integer> selectedList);

    List<GsmStsResultVM> staticsSpecialCellInSelList(String stsCode, List<Integer> selConfigs);
}
