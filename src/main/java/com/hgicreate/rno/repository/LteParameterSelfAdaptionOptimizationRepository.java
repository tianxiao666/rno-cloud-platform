package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.LteCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LteParameterSelfAdaptionOptimizationRepository extends JpaRepository<LteCell, Long> {

    /**
     * @param cellNameStartWithString 此参数需要自行写好匹配规则
     * @return
     */
    List<LteCell> findTop1000ByCellNameLike(String cellNameStartWithString);

}
