package com.hgicreate.rno.repository.indoor;


import com.hgicreate.rno.domain.indoor.CbFloor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface CbFloorRepository extends JpaRepository<CbFloor,Long>{

    /**
     * 保存楼层对象
     * @param s
     * @param <S>
     * @return
     */
    @Override
    <S extends CbFloor> S save(S s);

    /**
     * 获取符合匹配条件的楼层集合数据
     * @param buildingId
     * @param floorName
     * @param status
     * @return 匹配条件的楼层集合数据
     */
    public List<CbFloor> findTop1000ByBuildingIdAndFloorNameContainingAndStatusContaining(Long buildingId,String floorName,String status);

    /**
     * 删除楼层
     * @param floorId
     */
    public void deleteByFloorId(Long floorId);

    /**
     * 更新楼层状态
     * @param status
     * @param floorId
     * @return 1 成功 or 0 失败
     */
    @Modifying
    @Query("update CbFloor cf set cf.status = ?1 where cf.floorId =?2")
    public int updateFloorStatus(String status, Long floorId);

    /**
     * 获取楼层对象
     * @param floorId
     * @return 楼层对象
     */
    public CbFloor findByFloorId(Long floorId);

    /**
     * 更新楼层对象数据
     * @param floorName
     * @param floorType
     * @param physicalFloor
     * @param basement
     * @param floorNote
     * @param floorId
     * @return 1 成功 or 0 失败
     */
    @Modifying
    @Query("update CbFloor cf set cf.floorName=?1,cf.floorType=?2,cf.physicalFloor=?3,cf.basement=?4,cf.floorNote=?5  where cf.floorId =?6")
    public int updateFloor(String floorName,String floorType,String physicalFloor,String basement,String floorNote,Long floorId);

    /**
     * 获取楼层集合
     * @param buildingId
     * @return 楼层集合
     */
    public List<CbFloor> findByBuildingId(Long buildingId);

}
