package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmFasDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
public interface GsmFasDescRepository extends JpaRepository<GsmFasDesc, Long> {

    /**
     *
     * FAS数据查询(带有BSC参数)
     *
     * @param area 地区
     * @param bsc BSC
     * @param beginTestDate 查找测试开始时间
     * @param endTestDate 查找测试结束时间
     * @return FAS详细数据
     */
    List<GsmFasDesc> findTop1000ByAreaAndBscLikeAndMeaTimeBetween(Area area, String bsc, Date beginTestDate, Date endTestDate);
    /**
     * FAS数据查询(不带BSC参数)
     * @param area 地区
     * @param beginTestDate 查找测试开始时间
     * @param endTestDate 查找测试结束时间
     * @return FAS详细数据
     */
    List<GsmFasDesc> findTop1000ByAreaAndMeaTimeBetween(Area area, Date beginTestDate, Date endTestDate);

}
