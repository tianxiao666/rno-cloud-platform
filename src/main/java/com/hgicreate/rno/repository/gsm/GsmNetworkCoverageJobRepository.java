package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmNetworkCoverageJob;
import com.hgicreate.rno.domain.gsm.GsmStructAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author tao.xj
 */
@Repository
public interface GsmNetworkCoverageJobRepository extends JpaRepository<GsmNetworkCoverageJob,Long>{
    /**
     * 根据区域及创建时间查询网络覆盖分析任务
     * @param area 区域
     * @param begDate 开始时间
     * @param endDate 结束时间
     * @return 网络覆盖分析任务
     */
    List<GsmNetworkCoverageJob> findTop1000ByAreaAndCreatedDateBetweenOrderByCreatedDateDesc(
            Area area, Date begDate,Date endDate);


    /**
     * 根据区域查询网络覆盖分析任务
     * @param area 区域
     * @return 网络覆盖分析任务
     */
    List<GsmNetworkCoverageJob> findByAreaOrderByIdDesc(Area area);
}
