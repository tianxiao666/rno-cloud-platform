package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.LteNcellDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LteNcellDescRepository extends JpaRepository<LteNcellDesc, Long> {
    List<LteNcellDesc> findTop1000ByAreaAndDataTypeOrderByCreatedDateDesc(Area area, String dataType);

    List<LteNcellDesc> findTop1000ByAreaOrderByCreatedDateDesc(Area area);
}
