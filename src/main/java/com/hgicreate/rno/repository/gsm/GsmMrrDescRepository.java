package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
public interface GsmMrrDescRepository extends JpaRepository<GsmMrrDesc, Long>{
    /**
     * MRR数据查询(带有BSC参数)
     * @param area 地区
     * @param beginTestDate 查找开始时间（测试时间）
     * @param endTestDate 查找结束时间（测试时间）
     * @param factory 厂家
     * @param bsc bsc
     * @return MRR详细数据
     */
    List<GsmMrrDesc> findTop1000ByAreaAndFactoryAndBscLikeAndMeaDateBetween(Area area, String factory, String bsc, Date beginTestDate, Date endTestDate);

    /**
     * MRR数据查询(不带BSC参数)
     * @param area 地区
     * @param beginTestDate 查找开始时间（测试时间）
     * @param endTestDate 查找结束时间（测试时间）
     * @param factory 厂家
     * @return MRR详细数据
     */
    List<GsmMrrDesc> findTop1000ByAreaAndFactoryAndMeaDateBetween(Area area, String factory, Date beginTestDate, Date endTestDate);

    List<GsmMrrDesc> findTop1000ByAreaAndMeaDateBetween(Area area,Date begMeaDate,Date endMeaDate);
}
