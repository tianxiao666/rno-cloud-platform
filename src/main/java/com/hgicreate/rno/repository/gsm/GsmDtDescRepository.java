package com.hgicreate.rno.repository.gsm;


import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmDtDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
public interface GsmDtDescRepository extends JpaRepository<GsmDtDesc, Long> {
    /**
     * 查找DT数据列表(已指定任务名称)
     * @param area 地区
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param taskName 任务名称
     * @return DT数据列表
     */
    List<GsmDtDesc> findTop1000ByAreaAndNameLikeAndTestDateBetween(Area area, String taskName, Date beginDate, Date endDate);
    /**
     * 查找DT数据列表(未指定任务名称)
     * @param area 地区
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return DT数据列表
     */
    List<GsmDtDesc> findTop1000ByAreaAndTestDateBetween(Area area, Date beginDate, Date endDate);
}
