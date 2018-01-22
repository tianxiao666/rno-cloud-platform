package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmStructJobReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author tao.xj
 */
@Repository
public interface GsmStructJobReportRepository extends JpaRepository<GsmStructJobReport,Long>{
    /**
     * 根据结构优化分析任务id查找任务报告
     * @param jobId 任务id
     * @return 任务报告
     */
    List<GsmStructJobReport> findByGsmStructAnalysisJob_IdOrderByStatusDesc(Long jobId);
}
