package com.hgicreate.rno.service.gsm;

import java.util.List;
import java.util.Map;

public interface RnoCommonDao {

    /**
     * 通过区域ID获取指定区域的地图经纬度纠偏集合
     *
     * @author chao.xj
     * @date 2013-10-24下午02:10:37
     */
    public List<Map<String, Object>> getSpecialAreaRnoMapLnglatRelaGpsList(long areaid, String mapType);


}
