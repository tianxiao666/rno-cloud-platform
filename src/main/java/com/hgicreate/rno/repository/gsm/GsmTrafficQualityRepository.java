package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmTrafficQuality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GsmTrafficQualityRepository extends JpaRepository<GsmTrafficQuality, Long>{
    /**
     * 城市网络质量指标查询（已指定类别）
     * @param area 地区
     * @param beginTime 查找开始时间
     * @param latestAllowedTime 查找结束时间
     * @param type 类别
     * @return 城市网络质量指标
     */
    List<GsmTrafficQuality> findTop1000ByAreaAndTypeAndStaticTimeBetween(Area area, Integer type, Date beginTime, Date latestAllowedTime);
    /**
     * 城市网络质量指标查询（未指定类别）
     * @param area 地区
     * @param beginTime 查找开始时间
     * @param latestAllowedTime 查找结束时间
     * @return 城市网络质量指标
     */
    List<GsmTrafficQuality> findTop1000ByAreaAndStaticTimeBetween(Area area, Date beginTime, Date latestAllowedTime);
}
