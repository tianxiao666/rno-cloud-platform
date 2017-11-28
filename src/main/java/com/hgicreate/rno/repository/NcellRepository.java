package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.LteNcell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NcellRepository extends JpaRepository<LteNcell, Long> {

    List<LteNcell> findAllByCellId(String cellId);

}
