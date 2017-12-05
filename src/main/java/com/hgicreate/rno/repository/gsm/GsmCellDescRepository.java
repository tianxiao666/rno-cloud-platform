package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmCellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GsmCellDescRepository extends JpaRepository<GsmCellDesc,Long> {
}
