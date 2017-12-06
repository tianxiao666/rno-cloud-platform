package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.BscData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BscDataRepository extends JpaRepository<BscData,Long>{
    List<BscData> findByAreaAndStatus(Area area,String status);
}
