package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmPlaneLayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DmPlaneLayerRepository extends JpaRepository<DmPlaneLayer,Long>{

    public List<DmPlaneLayer> findByDrawMapId(String drawMapId);

    public List<DmPlaneLayer> findByDrawMapIdAndStatusOrderByLOrder(String drawMapId, String status);

    /**
     *@param statusNormal STATUS_NORMAL = "E";
     *@param statusCancel STATUS_CANCEL = "X";
     */
    @Modifying
    @Query("update DmPlaneLayer dp set dp.status = ?1 where dp.drawMapId =?2 and dp.status = ?3")
    public int updateBuildingStatus(String statusCancel, String drawMapId, String statusNormal);

    @Override
    <S extends DmPlaneLayer> List<S> save(Iterable<S> iterable);

    @Query(value = "Select SEQ_INDOOR_DM_PLANE_LAYER.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
