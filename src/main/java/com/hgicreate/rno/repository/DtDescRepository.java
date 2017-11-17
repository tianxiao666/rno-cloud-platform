package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.DtDesc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DtDescRepository extends PagingAndSortingRepository<DtDesc, Long> {

    List<DtDesc> findByAreaIdAndDataTypeInAndAreaTypeInAndCreatedDate
            (String areaId, String[] dataType, String[] areaType, Date createdDate, Pageable pageable);

    DtDesc findById(Long id);
}
