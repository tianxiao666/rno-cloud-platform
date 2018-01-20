package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.mapper.gsm.GsmFrequencyReuseMapper;
import com.hgicreate.rno.repository.gsm.GsmFrequencyReuseRepository;
import com.hgicreate.rno.web.rest.gsm.vm.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author xiao.sz
 */
@Slf4j
@Service
public class GsmFrequencyReuseServiceImpl implements GsmFrequencyReuseService {
    @Autowired
    private GsmFrequencyReuseMapper gsmFrequencyReuseMapper;
    @Autowired
    private GsmFrequencyReuseRepository gsmFrequencyReuseRepository;

    @Override
    public List<Map<String, Object>> getCellConfigAnalysisList() {
        return gsmFrequencyReuseRepository.getCellConfigAnalysisList();
    }

    @Override
    public Map<Integer, Object> staticsFreqReuseInfoInArea(String btsType, int currentPage, int pageSize, long areaId) {
        Map<Integer, Object> resultMap = null;
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = startIndex + pageSize;
        log.debug("startIndex = {}, endIndex = {}", startIndex, endIndex);
        Map<String, Object> m = new HashMap<>(4);
        m.put("btsType", "'" + btsType + "'");
        m.put("startIndex", startIndex);
        m.put("endIndex", endIndex);
        m.put("areaId", "'" + areaId + "'");
        List<Map<String, Object>> freqReuseLists = gsmFrequencyReuseMapper.selectBcchTchFrom(m);
        if (freqReuseLists != null || !freqReuseLists.isEmpty()) {
            resultMap = new TreeMap<>();
            // treeMap 排序
            for (Map<String, Object> map : freqReuseLists) {
                // bcch 频点
                String bcch = map.get("BCCH") + "";
                // tch 频点
                String tch = map.get("TCH") + "";
                // bcch 频点统计复用量
                if (!"".equals(bcch)) {
                    int key = Integer.valueOf(bcch);
                    if (resultMap.containsKey(key)) {
                        GsmFrequencyReuseInfoVM freInfo = (GsmFrequencyReuseInfoVM) resultMap.get(key);
                        freInfo.setBcchCount(freInfo.getBcchCount() + 1);
                    } else {
                        GsmFrequencyReuseInfoVM freInfo = new GsmFrequencyReuseInfoVM();
                        freInfo.setFreq(Integer.valueOf(bcch));
                        freInfo.setBcchCount(1);
                        freInfo.setTchCount(0);
                        resultMap.put(freInfo.getFreq(), freInfo);
                    }
                }
                // tch 频点统计复用量
                if (!"".equals(tch)) {
                    String[] tchArr = tch.split(",");
                    for (String tchValue : tchArr) {
                        if (!"".equals(tchValue)) {
                            int key = Integer.valueOf(tchValue);
                            if (resultMap.containsKey(key)) {
                                GsmFrequencyReuseInfoVM freInfo = (GsmFrequencyReuseInfoVM) resultMap.get(key);
                                freInfo.setTchCount(freInfo.getTchCount() + 1);
                            } else {
                                GsmFrequencyReuseInfoVM freInfo = new GsmFrequencyReuseInfoVM();
                                freInfo.setFreq(Integer.valueOf(tchValue));
                                freInfo.setTchCount(1);
                                freInfo.setBcchCount(0);
                                resultMap.put(freInfo.getFreq(), freInfo);
                            }
                        }
                    }
                }
            }
        }
        return resultMap;
    }
}
