package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.DtDataForDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DtDataForDetailRepository extends JpaRepository<DtDataForDetail, Long> {

    List<DtDataForDetail> findDtCellDetailById(Long id);

}
