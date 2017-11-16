package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DataJobRepository extends JpaRepository<DataJob, Long> {
     List<DataJob> findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
            Area area, String status, Date beginDate, Date endDate,String type);

    List<DataJob> findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
            Area area, Date beginDate, Date endDate, String type);

    DataJob findTopByStatusOrderById(String status);
}
