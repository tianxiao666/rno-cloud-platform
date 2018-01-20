package com.hgicreate.rno.repository.gsm;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiao.sz
 */
@Repository
public class GsmFrequencyReuseRepository {
    public List<Map<String, Object>> getCellConfigAnalysisList(){
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> one = new HashMap<>(8);
        one.put("collectTime","2017/12/24");
        one.put("title","GSM900小区");
        one.put("configId","9760-4043");
        one.put("selected",false);
        one.put("temp",false);
        one.put("type","CELLDATA");
        one.put("btsType","GSM900");
        one.put("name","系统配置");
        result.add(one);
        one = new HashMap<>(8);
        one.put("collectTime","2017/12/24");
        one.put("title","GSM1800小区");
        one.put("configId","9760-4042");
        one.put("selected",false);
        one.put("temp",false);
        one.put("type","CELLDATA");
        one.put("btsType","GSM1800");
        one.put("name","系统配置");
        result.add(one);

        return result;
    };
}
