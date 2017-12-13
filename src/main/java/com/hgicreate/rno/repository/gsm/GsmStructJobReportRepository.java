package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmStructJobReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GsmStructJobReportRepository extends JpaRepository<GsmStructJobReport,Long>{
    List<GsmStructJobReport> findByGsmStructAnalysisJob_Id(Long jobId);
}
