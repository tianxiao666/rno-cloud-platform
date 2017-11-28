package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.LteCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LteCellGisRepository extends JpaRepository<LteCell, Long> {

    List<LteCell> findOneByCellId(String cellId);

    List<LteCell> findAllByAreaId(Long areaId);
}
