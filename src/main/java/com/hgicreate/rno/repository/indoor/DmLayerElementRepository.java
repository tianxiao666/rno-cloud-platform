package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmLayerElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DmLayerElementRepository extends JpaRepository<DmLayerElement,Long>{

    public List<DmLayerElement> findByLayerId(String layerId);

    public List<DmLayerElement> findByDrawMapIdAndStatusOrderByElementId(String drawMapId, String status);

    /**
     *@param statusNormal STATUS_NORMAL = "E";
     *@param statusCancel STATUS_CANCEL = "X";
     */
    @Modifying
    @Query("update DmLayerElement de set de.status = ?1 where de.drawMapId =?2 and de.status = ?3")
    public int updateBuildingStatus(String statusCancel, String drawMapId, String statusNormal);

    @Override
    <S extends DmLayerElement> List<S> save(Iterable<S> iterable);

    @Query(value = "Select SEQ_INDOOR_DM_LAYER_ELEMENT.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
