package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.MrrDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
public interface MrrDescRepository extends JpaRepository<MrrDesc, Long>{
    List<MrrDesc> findByCityNameAndFactoryAndBscAndMeaDateBetween(String cityName, String factory, String bsc, Date beginTestDate, Date endTestDate);

    List<MrrDesc> findByCityNameAndFactoryAndMeaDateBetween(String cityName, String factory, Date beginTestDate, Date endTestDate);
}
