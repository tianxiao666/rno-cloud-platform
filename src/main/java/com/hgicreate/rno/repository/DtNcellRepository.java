package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.DtNcell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DtNcellRepository extends JpaRepository<DtNcell, Long> {

    List<DtNcell> findByDataIdInOrderByDataIdAsc(Long id);

}
