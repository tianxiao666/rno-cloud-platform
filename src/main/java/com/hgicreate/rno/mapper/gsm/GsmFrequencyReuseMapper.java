package com.hgicreate.rno.mapper.gsm;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GsmFrequencyReuseMapper {

    /**
     * 根据参数获取BCCH 和TCH
     * @param map
     * @return
     */
    List<Map<String, Object>> selectBcchTchFrom(Map<String, Object> map);
}
