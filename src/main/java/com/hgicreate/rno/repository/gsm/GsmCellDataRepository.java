package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GsmCellDataRepository extends JpaRepository<GsmCell, String> {
}
