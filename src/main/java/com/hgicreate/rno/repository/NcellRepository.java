package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Ncell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NcellRepository extends JpaRepository<Ncell, Long> {

    List<Ncell> findAllByCellId(String cellId);

}
