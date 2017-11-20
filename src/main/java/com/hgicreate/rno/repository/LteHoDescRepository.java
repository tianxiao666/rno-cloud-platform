package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.LteHoDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LteHoDescRepository extends JpaRepository<LteHoDesc,Long>{
    List<LteHoDesc> findTop1000ByAreaAndRecordDateBetweenOrderByCreatedDateDesc(Area area, Date beginDate, Date endDate);

    List<LteHoDesc> findTop1000ByAreaAndVendorAndRecordDateBetweenOrderByCreatedDateDesc(Area area,String vendor, Date beginDate,Date endDate);
}
