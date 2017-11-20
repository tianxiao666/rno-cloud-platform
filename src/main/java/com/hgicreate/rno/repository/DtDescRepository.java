package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.DtDesc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DtDescRepository extends PagingAndSortingRepository<DtDesc, Long> {

    List<DtDesc> findByArea_IdAndDataTypeInAndAreaTypeInAndCreatedDate
            (Long areaId, String[] dataType, String[] areaType, Date createdDate, Pageable pageable);

    DtDesc findById(Long id);

    List<DtDesc> findTop1000ByArea_IdAndDataTypeAndCreatedDateBetweenOrderByCreatedDateDesc(
            Long areaId, String dataType, Date begDate, Date endDate
    );

    List<DtDesc> findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
            Long areaId, Date begDate, Date endDate
    );
}
