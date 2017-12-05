package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GsmCellDataRepository extends JpaRepository<GsmCell, String> {

    List<GsmCell> findByCellId(String cellId);

    List<GsmCell> findByArea_Id(Long areaId);
}
