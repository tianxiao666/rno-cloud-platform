package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.CbPoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface CbPoiRepository extends JpaRepository<CbPoi,Long> {

    /**
     * 保存poi对象数据
     * @param s
     * @param <S>
     * @return
     */
    @Override
    <S extends CbPoi> S save(S s);

    /**
     * 获取符合匹配条件的兴趣点数据集合
     * @param buildingId
     * @param poiName
     * @param floorId
     * @param poiType
     * @param status
     * @return 匹配条件的兴趣点数据集合
     */
    public List<CbPoi> findTop1000ByBuildingIdAndPoiNameContainingAndFloorIdContainingAndPoiTypeContainingAndStatusContaining(Long buildingId, String poiName,
                                                                                                                              String floorId,String poiType,String status);

    /**
     * 删除兴趣点
     * @param poiId
     */
    public void deleteByPoiId(Long poiId);

    /**
     * 更新兴趣点状态
     * @param status
     * @param poiId
     * @return 1 成功 or 0 失败
     */
    @Modifying
    @Query("update CbPoi cp set cp.status = ?1 where cp.poiId =?2")
    public int updatePoiStatus(String status, Long poiId);

    /**
     * 查找兴趣点对象
     * @param poiId
     * @return 兴趣点对象
     */
    public CbPoi findByPoiId(Long poiId);

    /**
     * 获取兴趣点对象集合
     * @param drawMapId
     * @return 兴趣点对象集合
     */
    public List<CbPoi> findByDrawMapId(String drawMapId);

    /**
     * 删除兴趣点
     * @param drawMapId
     * @return
     */
    public int deleteByDrawMapId(String drawMapId);

    /**
     * 批量保存兴趣点集合数据
     * @param iterable
     * @param <S>
     * @return
     */
    @Override
    <S extends CbPoi> List<S> save(Iterable<S> iterable);

    /**
     * 获取兴趣点序列ID
     * @return 序列ID
     */
    @Query(value = "Select SEQ_INDOOR_CB_POI.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
