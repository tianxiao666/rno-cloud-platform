package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmNcellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GsmNcellDescRepository extends JpaRepository<GsmNcellDesc, Long> {
    List<GsmNcellDesc> findTop1000ByAreaAndDataTypeOrderByCreatedDateDesc(Area area, String dataType);

    List<GsmNcellDesc> findTop1000ByAreaOrderByCreatedDateDesc(Area area);

    List<GsmNcellDesc> findTop1000ByAreaAndCreatedDateBetween(Area area, Date beginTestDate, Date endTestDate);
}
