package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.GridData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GridDataRepository extends JpaRepository<GridData, Long> {

    List<GridData> findByGridTypeInAndAreaIdOrderByIdAsc(String[] gridType, Long areaId);

}
