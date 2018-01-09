package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamCheckVM;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

public interface GsmParamCheckService {
    /**
     * 查询一致性数据
     * @param vm
     * @return
     */
    List<Map<String, Object>> checkParamData(GsmParamCheckVM vm);
}
