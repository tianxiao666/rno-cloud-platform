package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.MrrDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
public interface MrrDescRepository extends JpaRepository<MrrDesc, Long>{
    List<MrrDesc> findByAreaAndFactoryAndBscAndMeaDateBetween(Area area, String factory, String bsc, Date beginTestDate, Date endTestDate);

    List<MrrDesc> findByAreaAndFactoryAndMeaDateBetween(Area area, String factory, Date beginTestDate, Date endTestDate);
}
