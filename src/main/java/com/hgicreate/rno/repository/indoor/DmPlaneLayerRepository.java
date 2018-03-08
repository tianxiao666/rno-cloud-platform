package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmPlaneLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface DmPlaneLayerRepository extends JpaRepository<DmPlaneLayer,Long>{

    /**
     * 获取图层集合数据
     * @param drawMapId
     * @return 图层集合数据
     */
    public List<DmPlaneLayer> findByDrawMapId(String drawMapId);

    /**
     * 获取匹配条件的图层集合数据
     * @param drawMapId
     * @param status
     * @return
     */
    public List<DmPlaneLayer> findByDrawMapIdAndStatusOrderByLOrder(String drawMapId,String status);

    /**
     * 更新图层状态
     *@param drawMapId
     *@param statusNormal STATUS_NORMAL = "E";
     *@param statusCancel STATUS_CANCEL = "X";
     *@return 1 成功 or 0 失败
     */
    @Modifying
    @Query("update DmPlaneLayer dp set dp.status = ?1 where dp.drawMapId =?2 and dp.status = ?3")
    public int updateBuildingStatus(String statusCancel, String drawMapId,String statusNormal);

    /**
     * 批保存图层集合数据
     * @param iterable
     * @param <S>
     * @return
     */
    @Override
    <S extends DmPlaneLayer> List<S> save(Iterable<S> iterable);

    /**
     * 获取图层序列ID
     * @return 序列ID
     */
    @Query(value = "Select SEQ_INDOOR_DM_PLANE_LAYER.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
