package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.web.rest.gsm.vm.GsmParamCheckVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.dh1
 */

@Mapper
public interface GsmParamCheckMapper {
    /**
     * 功率检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellPowerCheckResult(GsmParamCheckVM vm);

    /**
     * 跳频检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellFreqHopCheckResultTrue(GsmParamCheckVM vm);

    /**
     * 跳频检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellFreqHopCheckResult(GsmParamCheckVM vm);

    /**
     * NCCPERM检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellNccpermResult(GsmParamCheckVM vm);

    /**
     * 测量频点多定义
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellMeaFreqResult(GsmParamCheckVM vm);

    /**
     * BA表个数检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellBaNumCheckResult(GsmParamCheckVM vm);

    /**
     * TALIM_MAXTA检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellTalimAndMaxtaCheckResult(GsmParamCheckVM vm);

    /**
     * 同频同bsic检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellCoBsicCheckResult(GsmParamCheckVM vm);

    /**
     * 邻区过多过少检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellNcellNumCheckResult(GsmParamCheckVM vm);

    /**
     * 本站邻区漏定义
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellNcellMomitCheckResult(GsmParamCheckVM vm);

    /**
     * 单向邻区检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellUnidirNcellResult(GsmParamCheckVM vm);

    /**
     * 同邻频检查
     *
     * @param vm 参数对象
     * @return 查询结果list
     */
    List<Map<String, Object>> getEriCellSameNcellFreqData(GsmParamCheckVM vm);
}
