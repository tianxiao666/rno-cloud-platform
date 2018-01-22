package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmDtSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zeng.dh1
 */

@Repository
public interface GsmDtAnalysisRepository extends JpaRepository<GsmDtSample, Long>  {

    /**
     * 根据区域id查询采样数据
     * @param id
     * @return  采样数据list
     */
    List<GsmDtSample> findAllByGsmDtDesc_AreaIdOrderBySampleTime(Long id);

    /**
     * 根据id查询采样数据详情
     * @param id
     * @return 数据详情list
     */
    List<GsmDtSample> findAllById(Long id);

}
