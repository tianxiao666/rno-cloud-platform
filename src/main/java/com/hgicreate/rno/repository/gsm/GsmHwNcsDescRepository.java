package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmHwNcsDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GsmHwNcsDescRepository extends JpaRepository<GsmHwNcsDesc, Long> {
    /**
     * 厂家为华为的NCS数据查询（带有BSC）
     * @param area 地区
     * @param beginTestDate 查找开始时间（测试时间）
     * @param endTestDate 查找结束时间（测试时间）
     * @param bsc bsc
     * @return 华为的NCS数据
     */
    List<GsmHwNcsDesc> findTop1000ByAreaAndBscLikeAndMeaTimeBetween(Area area, String bsc, Date beginTestDate, Date endTestDate);
    /**
     * 厂家为华为的NCS数据查询（不带BSC）
     * @param area 地区
     * @param beginTestDate 查找开始时间（测试时间）
     * @param endTestDate 查找结束时间（测试时间）
     * @return 华为的NCS数据
     */
    List<GsmHwNcsDesc> findTop1000ByAreaAndMeaTimeBetween(Area area, Date beginTestDate, Date endTestDate);
}
