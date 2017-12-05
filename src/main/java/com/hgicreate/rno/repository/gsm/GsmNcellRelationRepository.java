package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmNcellRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GsmNcellRelationRepository  extends JpaRepository<GsmNcellRelation,Long> {
    List<GsmNcellRelation> findByCellId(String cellId);
}
