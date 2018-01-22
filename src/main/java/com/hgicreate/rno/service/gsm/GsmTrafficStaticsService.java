package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.service.gsm.dto.GsmStsResultDTO;

import java.util.List;
import java.util.Map;

/**
 * @author xiao.sz
 */
public interface GsmTrafficStaticsService {

    /**
     * 根据id 串获取任务配置列表
     *
     * @param configIds 来自页面的id串
     * @return 返回任务配置列表
     */
    List<Map<String, Object>> getCellAudioOrDataDescByConfigIds(final String configIds);

    /**
     * 统计资源利用率
     *
     * @param stsCode      点击的按钮的操作识别码
     * @param selectedList 列表的勾选结果集
     * @return 结果集
     */

    List<GsmStsResultDTO> staticsResourceUtilizationRateInSelectList(String stsCode, List<Integer> selectedList);

    /**
     * 统计符合某种要求的小区
     *
     * @param stsCode    点击的按钮的操作识别码
     * @param selConfigs 列表的勾选结果集
     * @return 结果集
     */
    List<GsmStsResultDTO> staticsSpecialCellInSelectList(String stsCode, List<Integer> selConfigs);
}
