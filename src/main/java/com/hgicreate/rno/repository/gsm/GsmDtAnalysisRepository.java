package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmDtSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GsmDtAnalysisRepository extends JpaRepository<GsmDtSample, Long>  {

    List<GsmDtSample> findAllByGsmDtDesc_AreaId(Long id);
    List<GsmDtSample> findAllById(Long id);

}
