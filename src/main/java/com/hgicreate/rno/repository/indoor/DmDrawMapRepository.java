package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmDrawMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface DmDrawMapRepository extends JpaRepository<DmDrawMap,Long>{

    /**
     * 保存绘图数据
     * @param s 绘图对象
     * @param <S>
     * @return
     */
    @Override
    <S extends DmDrawMap> S save(S s);

    /**
     * 获取绘图对象集合数据
     * @param buildingId
     * @param dmTopic
     * @param floorId
     * @param status
     * @return 绘图对象集合数据
     */
    public List<DmDrawMap> findTop1000ByBuildingIdAndDmTopicContainingAndFloorIdContainingAndStatusContaining(Long buildingId, String dmTopic,
                                                                                                              String floorId, String status);

    /**
     * 通过ID删除绘图对象
     * @param drawMapId
     */
    public void deleteByDrawMapId(Long drawMapId);

    /**
     * 更新绘图对象状态
     * @param status
     * @param drawMapId
     * @return 0 or 1
     */
    @Modifying
    @Query("update DmDrawMap dd set dd.status = ?1 where dd.drawMapId =?2")
    public int updateDmDrawMapStatus(String status, Long drawMapId);

    /**
     * 获取绘图对象
     * @param drawMapId
     * @return 绘图对象
     */
    public DmDrawMap findByDrawMapId(Long drawMapId);

    /**
     * 获取绘图对象集合数据
     * @param floorId
     * @return 绘图对象集合数据
     */
    public List<DmDrawMap> findByFloorId(String floorId);

    /**
     * 批保存绘图对象
     * @param iterable
     * @param <S>
     * @return
     */
    @Override
    <S extends DmDrawMap> List<S> save(Iterable<S> iterable);

    /**
     * 更新绘图对象
     * @param buildingId
     * @param floorId
     * @param dmTopic
     * @param dwScale
     * @param dwUnit
     * @param height
     * @param width
     * @param picId
     * @param dmNote
     * @param status
     * @param drawMapId
     * @return 1 成功 or 0 失败
     */
    @Modifying
    @Query("update DmDrawMap dm set dm.buildingId = ?1,dm.floorId = ?2,dm.dmTopic = ?3,dm.dwScale = ?4,dm.dwUnit = ?5,dm.height = ?6," +
            "dm.width = ?7,dm.picId = ?8,dm.dmNote = ?9,dm.status = ?10,dm.modTime='date ( \"Y/m/d H:i:s\" )' where dm.drawMapId =?11")
    public int updateDmDrawMap(Long buildingId, String floorId,String dmTopic,String dwScale,String dwUnit,
                               String height,String width,String picId,String dmNote,String status,Long drawMapId);

    /**
     * 获取绘图表序列ID
     * @return 序列ID
     */
    @Query(value = "Select SEQ_INDOOR_DM_DRAW_MAP.nextval as id from dual",nativeQuery = true)
    public long getSeqId();

    /**
     * MT API
     * @param floorIds
     * @param statusA 正常->A
     * @return
     */
    public List<DmDrawMap> getAllByFloorIdInAndStatus(String floorIds,String statusA);
}
