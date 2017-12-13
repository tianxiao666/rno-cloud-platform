package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmDynamicCoverageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository(value = "rnoCommonDao")
@Scope("prototype")
@Slf4j
public class RnoCommonDaoImpl implements RnoCommonDao {

    @Autowired
    private GsmDynamicCoverageMapper ncsMapper;

    /**
     * 通过区域ID获取指定区域的地图经纬度纠偏集合
     *
     * @author chao.xj
     * @date 2013-10-24下午02:10:37
     */

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getSpecialAreaRnoMapLnglatRelaGpsList(long areaid, String mapType) {

        log.info("进入方法：getSpecialAreaRnoMapLnglatRelaGpsList。areaId=" + areaid + ",mapType=" + mapType);
        mapType = "'" + mapType + "'";
        Map<String, Object> map = new HashMap<>();
        map.put("areaId", areaid);
        map.put("mapType", mapType);
        List<Map<String, Object>> resultList = ncsMapper.selectAllFromRnoGSMMapLngLatRelaGps(map);
        log.debug("完成数据库查询：getSpecialAreaRnoMapLnglatRelaGpsList = {}", resultList);
        return resultList;

    }
}