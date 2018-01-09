package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrAnalysisQueryVM;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

public interface GsmMrrAnalysisService {
    /**
     * 查询当前区域下的bsc
     * @param areaId
     * @return
     */
    Map<String, List<Map<String, Object>>> queryAllBscByAreaId(String areaId);

    /**
     * 查询MRR指标分析数据
     * @param vm
     * @return
     */
    List<Map<String, Object>> queryEriMrrData(GsmMrrAnalysisQueryVM vm);
}
