package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.LteMrDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LteMrDescRepository extends JpaRepository<LteMrDesc,Long> {
    List<LteMrDesc> findTop1000ByAreaAndRecordDateBetweenOrderByCreatedDateDesc(Area area, Date beginDate,Date endDate);

    List<LteMrDesc> findTop1000ByAreaAndVendorAndRecordDateBetweenOrderByCreatedDateDesc(Area area,String vendor, Date beginDate,Date endDate);
}
