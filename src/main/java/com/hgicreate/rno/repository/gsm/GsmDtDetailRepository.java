package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmDtData;
import com.hgicreate.rno.domain.gsm.GsmDtSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GsmDtDetailRepository extends JpaRepository<GsmDtSample, Long>  {

    List<GsmDtSample> findAllByGsmDtData_Id(Long id);

}
