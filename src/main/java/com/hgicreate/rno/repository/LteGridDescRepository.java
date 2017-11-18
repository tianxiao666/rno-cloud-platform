package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.LteGridDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LteGridDescRepository extends JpaRepository<LteGridDesc,Long>{
    List<LteGridDesc> findTop1000ByAreaOrderByCreatedDateDesc(Area area);

    List<LteGridDesc> findTop1000ByAreaAndGridTypeOrderByCreatedDateDesc(Area area,String gridType);
}
