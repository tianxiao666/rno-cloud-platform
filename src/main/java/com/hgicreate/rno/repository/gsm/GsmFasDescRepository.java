package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmFasDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GsmFasDescRepository extends JpaRepository<GsmFasDesc, Long> {
    List<GsmFasDesc> findTop1000ByAreaAndBscAndMeaTimeBetween(Area area, String bsc, Date beginTestDate, Date endTestDate);
    List<GsmFasDesc> findTop1000ByAreaAndMeaTimeBetween(Area area, Date beginTestDate, Date endTestDate);

}
