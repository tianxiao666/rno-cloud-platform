package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.LteTrafficData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LteTrafficDataRepository extends JpaRepository<LteTrafficData,Long>{
     List<LteTrafficData> findTop1000ByLteTrafficDesc_AreaIdAndLteTrafficDesc_BeginTimeAfterAndLteTrafficDesc_EndTimeBeforeAndPmUserLabelIn(
            Long areaId, Date beginTime,Date endTime,String[] cellName) ;

     List<LteTrafficData> findByLteTrafficDesc_AreaIdAndLteTrafficDesc_BeginTimeAfterAndLteTrafficDesc_EndTimeBeforeAndPmUserLabelIn(
            Long areaId, Date beginTime,Date endTime,String[] cellName) ;

     List<LteTrafficData> findByCellIdInOrderByLteTrafficDesc_BeginTimeDesc(String[] cellId);

     List<LteTrafficData> findByLteTrafficDesc_BeginTimeAndCellId(
             Date beginTime, String cellId);

     List<LteTrafficData> findByLteTrafficDesc_AreaId(Long areaId);

     List<LteTrafficData> findByCellIdAndLteTrafficDesc_BeginTime(String cellId, Date beginTime);
}
