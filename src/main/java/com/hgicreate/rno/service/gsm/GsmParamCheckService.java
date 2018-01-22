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
     * @param vm 查询条件对象
     * @return 查询结果list
     */
    List<Map<String, Object>> checkParamData(GsmParamCheckVM vm);
    /**
     * 获取sheetname名字
     * @param name 核查类型
     * @return sheetname
     */
    String getSheetName(String name);
}
