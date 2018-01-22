package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmCellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author tao.xj
 */
@Repository
public interface GsmCellDescRepository extends JpaRepository<GsmCellDesc,Long> {
    /**
     * 根据区域及创建时间查询小区描述信息
     * @param areaId 区域id
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 小区描述信息对象
     */
    List<GsmCellDesc> findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
            Long areaId, Date beginTime, Date endTime);
}
