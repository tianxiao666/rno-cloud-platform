package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmInterferMatrixJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author tao.xj
 */
@Repository
public interface GsmInterferMatrixJobRepository extends JpaRepository<GsmInterferMatrixJob,Long>{
    /**
     * 根据区域及创建时间查询干扰矩阵任务
     * @param area 区域
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return 干扰矩阵任务
     */
    List<GsmInterferMatrixJob> findTop1000ByAreaAndCreatedDateBetweenOrderByCreatedDateDesc(Area area, Date beginDate,Date endDate);

    List<GsmInterferMatrixJob> findTop1000ByAreaAndDataTypeAndAndCreatedDateBetweenOrderByCreatedDateDesc(
            Area area, String dataType,Date beginDate,Date endDate);

    List<GsmInterferMatrixJob> findByAreaOrderByIdDesc(Area area);
}
