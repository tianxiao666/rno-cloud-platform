package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmTrafficQuality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GsmTrafficQualityRepository extends JpaRepository<GsmTrafficQuality, Long>{
    List<GsmTrafficQuality> findTop1000ByAreaAndTypeAndStaticTimeBetween(Area area, Integer type, Date beginTime, Date latestAllowedTime);
    List<GsmTrafficQuality> findTop1000ByAreaAndStaticTimeBetween(Area area, Date beginTime, Date latestAllowedTime);
}
