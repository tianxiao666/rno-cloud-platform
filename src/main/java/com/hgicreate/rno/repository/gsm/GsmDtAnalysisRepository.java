package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmDtData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GsmDtAnalysisRepository extends JpaRepository<GsmDtData, Long>  {

    List<GsmDtData> findAllByGsmDtDesc_AreaId(Long id);

}
