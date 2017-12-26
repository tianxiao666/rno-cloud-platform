package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmEriNcs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GsmEriNcsRepository extends JpaRepository<GsmEriNcs, Long> {
    List<GsmEriNcs> findTop1000ByAreaAndCell(Area area, String cell);
}
