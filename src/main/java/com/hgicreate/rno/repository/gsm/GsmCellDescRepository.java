package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmCellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GsmCellDescRepository extends JpaRepository<GsmCellDesc,Long> {
    List<GsmCellDesc> findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
            Long areaId, Date beginTime, Date endTime);
}
