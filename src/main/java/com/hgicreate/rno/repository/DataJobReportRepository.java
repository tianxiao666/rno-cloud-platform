package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.DataJobReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataJobReportRepository extends JpaRepository<DataJobReport,Long> {

    List<DataJobReport> findByDataJob_Id(Long id);
}
