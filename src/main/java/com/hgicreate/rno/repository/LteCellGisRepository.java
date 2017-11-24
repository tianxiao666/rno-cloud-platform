package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LteCellGisRepository extends JpaRepository<Cell, Long> {

    List<Cell> findOneByCellId(String cellId);

    List<Cell> findAllByAreaId(Long areaId);
}
