package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.ApEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface ApEquipmentRepository extends JpaRepository<ApEquipment,Long> {

    /**
     * 保存定位设备数据对象
     * @param s
     * @param <S>
     * @return
     */
    @Override
    <S extends ApEquipment> S save(S s);

    /**
     * 获取符合匹配条件的定位数据集合
     * @param buildingId
     * @param equtSsid
     * @param floorId
     * @param equtType
     * @param status
     * @return
     */
    public List<ApEquipment> findTop1000ByBuildingIdAndEqutSsidContainingAndFloorIdContainingAndEqutTypeContainingAndStatusContaining(String buildingId, String equtSsid,
                                                                                                                                      String floorId, String equtType, String status);

    /**
     * 删除定位数据
     * @param apId
     */
    public void deleteByApId(Long apId);

    /**
     * 更新定位设备状态
     * @param status
     * @param apId
     * @return 1 or 0
     */
    @Modifying
    @Query("update ApEquipment ap set ap.status = ?1 where ap.apId =?2")
    public int updateApStatus(String status, Long apId);

    /**
     * 获取定位设备对象
     * @param apId
     * @return 定位设备对象
     */
    public ApEquipment findByApId(Long apId);

    /**
     * 获取定位设备集合数据
     * @param drawMapId
     * @return 定位设备集合数据
     */
    public List<ApEquipment> findByDrawMapId(String drawMapId);

    /**
     * 删除定位设备
     * @param drawMapId
     * @return 1 成功 or 0 失败
     */
    public int deleteByDrawMapId(String drawMapId);

    /**
     * 批保存定位设备集合数据
     * @param iterable
     * @param <S>
     * @return
     */
    @Override
    <S extends ApEquipment> List<S> save(Iterable<S> iterable);

    /**
     * 获取定位设备序列ID
     * @return 序列ID
     */
    @Query(value = "Select SEQ_INDOOR_AP_EQUIPMENT.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
