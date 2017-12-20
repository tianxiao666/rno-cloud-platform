package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmLteNcellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GsmLteNcellDescRepository extends JpaRepository<GsmLteNcellDesc, Long> {
    List<GsmLteNcellDesc> findTop1000ByAreaAndCreatedDateBetween(Area area, Date beginTestDate, Date endTestDate);
}
