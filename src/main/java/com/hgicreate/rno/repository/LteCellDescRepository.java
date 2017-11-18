package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.LteCellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LteCellDescRepository extends JpaRepository<LteCellDesc,Long>{

    List<LteCellDesc> findTop1000ByArea_IdAndDataTypeAndCreatedDateBetweenOrderByCreatedDateDesc(
      Long areaId,String dataType, Date beginTime, Date endTime);
}
