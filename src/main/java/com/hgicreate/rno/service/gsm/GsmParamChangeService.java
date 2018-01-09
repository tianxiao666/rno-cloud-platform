package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamChangeVM;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

public interface GsmParamChangeService {
    /**
     * 差异对比查询
     * @param vm
     * @return
     */
    List<Map<String, Object>> changeParamData(GsmParamChangeVM vm);

    /**
     * 参数核查详情
     * @param vm
     * @return
     */
    List<Map<String, Object>> queryParamDeatialData(GsmParamChangeVM vm);

    /**
     * 查询当前日期是否有小区数据
     * @param vm
     * @return
     */
    Boolean queryDateExist(GsmParamChangeVM vm);

    /**
     * 导出参数差异
     * @param vm
     * @return
     */
    List<Map<String, Object>> exportChangeParamData(GsmParamChangeVM vm);
}
