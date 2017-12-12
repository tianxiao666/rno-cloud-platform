package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmStructAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GsmStructAnalysisJobRepository extends JpaRepository<GsmStructAnalysisJob,Long>{
}
