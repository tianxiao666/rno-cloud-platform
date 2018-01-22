package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmStsDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author yang.ch1
 */
@Repository
public interface GsmStsDescriptorRepository extends JpaRepository<GsmStsDescriptor,Long>{
    /**
     * 获取某一时间段话务数据描述信息
     * @param areaId 区域id
     * @param stsDateFrom 话务数据开始日期
     * @param stsDateTo 话务数据结束日期
     * @param specType 话务数据类型
     * @param stsPeriod 话务数据时间段
     * @return 话务数据描述对象集合
     */
    List<GsmStsDescriptor> findTop1000ByArea_IdAndStsDateBetweenAndSpecTypeAndStsPeriodOrderByCreateTimeDesc(
            Long areaId, Date stsDateFrom,Date stsDateTo,String specType,String stsPeriod
    );

    /**
     * 获取全时段话务数据描述信息
     * @param areaId 区域id
     * @param stsDateFrom 话务数据开始日期
     * @param stsDateTo 话务数据结束日期
     * @param specType 话务数据类型
     * @return 话务数据描述对象集合
     */
    List<GsmStsDescriptor> findTop1000ByArea_IdAndStsDateBetweenAndSpecTypeOrderByCreateTimeDesc(
            Long areaId, Date stsDateFrom,Date stsDateTo,String specType
    );
}
