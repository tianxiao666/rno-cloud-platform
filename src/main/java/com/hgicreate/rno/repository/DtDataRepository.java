package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.DtData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DtDataRepository extends JpaRepository<DtData, Long> {

    List<DtData> findAllByDescIdInOrderByIdAsc(Long[] id);

    List<DtData> findAllByDescIdOrderByIdAsc(Long id);

    List<DtData> findByDescIdInAndEarfcnIn(Long[] descId, Integer[] earfcn);

    List<DtData> findByDescIdAndRsrpLessThanOrDescIdAndRsSinrLessThanOrderByIdAsc(
            Long descId1, Integer rsrp, Long descId2, Integer rsSinr);

}
