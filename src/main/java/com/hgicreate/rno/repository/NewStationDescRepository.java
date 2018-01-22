package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.NewStationDesc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

/**
 * @author ke_weixu
 */
public interface NewStationDescRepository extends JpaRepository<NewStationDesc,Long> {
    List<NewStationDesc> findByAreaIdAndFileCodeAndTestTimeBetween(Long areaId, String fileCode, Date testBeginTime, Date testEndTime);
}
