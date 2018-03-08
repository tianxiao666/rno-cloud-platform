package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmLayerElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface DmLayerElementRepository extends JpaRepository<DmLayerElement,Long>{

    /**
     * 获取图层元素集合
     * @param layerId
     * @return 图层元素集合
     */
    public List<DmLayerElement> findByLayerId(String layerId);

    /**
     * 获取匹配条件的图层元素集合
     * @param drawMapId
     * @param status
     * @return 排序图层元素集合
     */
    public List<DmLayerElement> findByDrawMapIdAndStatusOrderByElementId(String drawMapId,String status);

    /**
     * 更新场所状态
     *@param drawMapId
     *@param statusNormal STATUS_NORMAL = "E";
     *@param statusCancel STATUS_CANCEL = "X";
     *@return 1 成功 or 0 失败
     */
    @Modifying
    @Query("update DmLayerElement de set de.status = ?1 where de.drawMapId =?2 and de.status = ?3")
    public int updateBuildingStatus(String statusCancel, String drawMapId,String statusNormal);

    /**
     * 批保存图层元素数据集合
     * @param iterable
     * @param <S>
     * @return
     */
    @Override
    <S extends DmLayerElement> List<S> save(Iterable<S> iterable);

    /**
     * 获取图层元素序列ID
     * @return 序列ID
     */
    @Query(value = "Select SEQ_INDOOR_DM_LAYER_ELEMENT.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
