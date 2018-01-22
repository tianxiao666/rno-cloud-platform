package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmBscData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zeng.dh1
 */

@Repository
public interface GsmBscDataRepository extends JpaRepository<GsmBscData,Long>{
    /**
     * 根据区域和状态查询所有bsc
     * @param area
     * @param status
     * @return 符合条件的bsc
     */
    List<GsmBscData> findByAreaAndStatus(Area area, String status);
}
