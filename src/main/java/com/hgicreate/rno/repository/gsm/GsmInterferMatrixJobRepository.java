package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmInterferMatrixJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GsmInterferMatrixJobRepository extends JpaRepository<GsmInterferMatrixJob,Long>{
    List<GsmInterferMatrixJob> findTop1000ByAreaAndCreatedDateBetweenOrderByCreatedDateDesc(Area area, Date beginDate,Date endDate);

    List<GsmInterferMatrixJob> findTop1000ByAreaAndDataTypeAndAndCreatedDateBetweenOrderByCreatedDateDesc(
            Area area, String dataType,Date beginDate,Date endDate);

    List<GsmInterferMatrixJob> findByAreaOrderByIdDesc(Area area);
}
