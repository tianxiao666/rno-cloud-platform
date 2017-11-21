package com.hgicreate.rno.mapper;

import com.hgicreate.rno.domain.LteTrafficData;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface LteTrafficDataQueryMapper {
    List<LteTrafficData> queryTop1000TrafficData(Long areaId,Date beginTime, Date endTime);
}
