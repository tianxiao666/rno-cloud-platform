package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmBscData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GsmBscDataRepository extends JpaRepository<GsmBscData,Long>{
    List<GsmBscData> findByAreaAndStatus(Area area, String status);
}
