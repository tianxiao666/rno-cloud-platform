package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmDynamicCoverageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AuthDsDataDaoImpl {

    @Autowired
    private GsmDynamicCoverageMapper gsmDynamicCoverageMapper;

    public Map<Long, List<Long>> cityToSubAreaS = new HashMap<Long, List<Long>>();

    volatile static boolean hasInit = false;

    /**
     * 获取包括自身以及子区域id 在内的逗号分隔的字符串
     *
     * @param parId
     * @return
     * @author brightming
     * 2014-9-29 上午11:31:06
     */
    public String getSubAreaAndSelfIdListStrByParentId(long parId) {
        log.debug("==={}", gsmDynamicCoverageMapper);
        List<Long> subIds = null;
        List<Map<String, Object>> areaMaps = gsmDynamicCoverageMapper.selectFromSysAreaOrderByParentId();
        if (areaMaps != null) {
            log.debug("areaMaps is not null!!!!!!!!!!!!!!");
            log.debug("areaMaps ID={}", areaMaps.get(0));
            long id = -1, parentId = -1;
            for (Map<String, Object> areaMap : areaMaps) {
                if (areaMap.get("ID") != null) {
                    try {
                        id = Long.parseLong(areaMap.get("ID")
                                .toString());
                        parentId = Long.parseLong(areaMap.get("PARENT_ID").toString());
                        subIds = cityToSubAreaS.get(parentId);
                        if (subIds == null) {
                            subIds = new ArrayList<Long>();
                            cityToSubAreaS.put(parentId, subIds);
                        }
                        subIds.add(id);
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                }
            }
        }

        log.debug("subIds == {}", subIds);
        if (subIds != null && subIds.size() > 0) {
            String tmp = parId + ",";
            for (long id : subIds) {
                tmp += id + ",";
            }
            if (tmp.length() > 0) {
                tmp = tmp.substring(0, tmp.length() - 1);
            }
            return tmp;
        } else {
            log.debug("nulllllllllllllll");
            return null;
        }
    }

    /**
     * 获取某个区域的详情
     *
     * @param areaId
     * @return
     * @author brightming 2014-9-28 下午6:01:38
     */
    public Map<String, Object> getAreaData(long areaId) {
        List<Map<String, Object>> areaMaps = gsmDynamicCoverageMapper.selectFromSysAreaOrderByParentId();
        Map<String, Object> result = null;
        for (Map<String, Object> areaMap : areaMaps) {
            if (areaMap.get("ID") != null) {
                try {
                    result = areaMap;
                    break;
                } catch (Exception e) {

                }
            }
        }
        return result;
    }
}
