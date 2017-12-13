package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.gsm.AreaRectangle;
import com.hgicreate.rno.web.rest.gsm.vm.Area;
import com.hgicreate.rno.web.rest.gsm.vm.DynamicCoverageResult;

import java.util.List;
import java.util.Map;

public interface RnoLteGisService {

    public List<Area> getSpecialSubAreasByAccountAndParentArea(String account, long parentAreaId, String subAreaLevel);

    /**
     * 通过区域ID获取指定区域的地图经纬度纠偏map集合
     *
     * @param areaid
     * @param mapType 地图类型
     * @return
     * @author chao.xj
     * @date 2013-10-28上午10:49:29
     */
    public Map<AreaRectangle, List<Map<String, Object>>> getSpecialAreaRnoMapLnglatRelaGpsMapList(long areaid,
                                                                                                  String mapType);


    /**
     * 获取画LTE小区动态覆盖图(折线)所需的数据
     */
    DynamicCoverageResult get4GDynaCoverageData2ByCityAndDate(String lteCellId, long cityId, String dates, double imgCoeff, double imgSizeCoeff);

    /**
     * 获取画LTE小区动态覆盖 所需的数据[邻区位置]
     */
    List<Map<String, String>> getLteDynamicCoverageShapeData(String lteCellId, long cityId, String dates, String inOrOut);

    /* PCI评估 */

    /**
     * 转换lte小区与某同站小区的pci
     */
    Map<String, Boolean> changeLteCellPci(String cell1, String pci1, String cell2, String pci2);

    /**
     * 获取IN干扰小区信息
     */
    List<Map<String, Object>> getInCellInfo(int jobId, String cellId);

    /**
     * 获取OUT干扰小区信息
     */
    List<Map<String, Object>> getOutCellInfo(int jobId, String ncellId);

    /**
     * 获取PCI智能优化小区信息
     */
    List<Map<String, Object>> getPciCellInfo(int jobId, String cellId);
}
