package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmNetworkCoverageJob;
import com.hgicreate.rno.domain.gsm.GsmStructAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GsmNetworkCoverageJobRepository extends JpaRepository<GsmNetworkCoverageJob,Long>{
    List<GsmNetworkCoverageJob> findTop1000ByAreaAndCreatedDateBetweenOrderByCreatedDateDesc(
            Area area, Date begDate,Date endDate);

    List<GsmNetworkCoverageJob> findByAreaOrderByIdDesc(Area area);
}
