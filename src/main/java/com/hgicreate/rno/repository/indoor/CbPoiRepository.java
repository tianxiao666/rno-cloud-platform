package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.CbPoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbPoiRepository extends JpaRepository<CbPoi,Long> {

    @Override
    <S extends CbPoi> S save(S s);

    public List<CbPoi> findTop1000ByBuildingIdAndPoiNameContainingAndFloorIdContainingAndPoiTypeContainingAndStatusContaining(Long buildingId, String poiName,
                                                                                                                              String floorId, String poiType, String status);
    public void deleteByPoiId(Long poiId);

    @Modifying
    @Query("update CbPoi cp set cp.status = ?1 where cp.poiId =?2")
    public int updatePoiStatus(String status, Long poiId);

    public CbPoi findByPoiId(Long poiId);

    public List<CbPoi> findByDrawMapId(String drawMapId);

    public int deleteByDrawMapId(String drawMapId);

    @Override
    <S extends CbPoi> List<S> save(Iterable<S> iterable);

    @Query(value = "Select SEQ_INDOOR_CB_POI.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
