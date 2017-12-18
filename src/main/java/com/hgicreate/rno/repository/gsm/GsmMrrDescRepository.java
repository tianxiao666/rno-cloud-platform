package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
public interface GsmMrrDescRepository extends JpaRepository<GsmMrrDesc, Long>{
    List<GsmMrrDesc> findTop1000ByAreaAndFactoryAndBscLikeAndMeaDateBetween(Area area, String factory, String bsc, Date beginTestDate, Date endTestDate);

    List<GsmMrrDesc> findTop1000ByAreaAndFactoryAndMeaDateBetween(Area area, String factory, Date beginTestDate, Date endTestDate);

    List<GsmMrrDesc> findTop1000ByAreaAndMeaDateBetween(Area area,Date begMeaDate,Date endMeaDate);
}
