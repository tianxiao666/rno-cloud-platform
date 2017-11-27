package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.NcellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LteNcellDescRepository extends JpaRepository<NcellDesc, Long> {
    List<NcellDesc> findTop1000ByAreaAndDataTypeOrderByCreatedDateDesc(Area area,String dataType);

    List<NcellDesc> findTop1000ByAreaOrderByCreatedDateDesc(Area area);
}
