package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmStsDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GsmStsDescriptorRepository extends JpaRepository<GsmStsDescriptor,Long>{
    List<GsmStsDescriptor> findTop1000ByArea_IdAndStsDateBetweenAndSpecTypeAndStsPeriodOrderByCreateTimeDesc(
            Long areaId, Date stsDateFrom,Date stsDateTo,String specType,String stsPeriod
    );

    List<GsmStsDescriptor> findTop1000ByArea_IdAndStsDateBetweenAndSpecTypeOrderByCreateTimeDesc(
            Long areaId, Date stsDateFrom,Date stsDateTo,String specType
    );
}
