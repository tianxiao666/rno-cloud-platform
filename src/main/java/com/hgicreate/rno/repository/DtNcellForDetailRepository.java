package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.DtNcellForDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DtNcellForDetailRepository extends JpaRepository<DtNcellForDetail, Long> {

    List<DtNcellForDetail> findDtNCellDetailById(Long id);

}
