package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmDrawMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DmDrawMapRepository extends JpaRepository<DmDrawMap,Long>{

    @Override
    <S extends DmDrawMap> S save(S s);

    public List<DmDrawMap> findTop1000ByBuildingIdAndDmTopicContainingAndFloorIdContainingAndStatusContaining(Long buildingId, String dmTopic,
                                                                                                              String floorId, String status);
    public void deleteByDrawMapId(Long drawMapId);

    @Modifying
    @Query("update DmDrawMap dd set dd.status = ?1 where dd.drawMapId =?2")
    public int updateDmDrawMapStatus(String status, Long drawMapId);

    public DmDrawMap findByDrawMapId(Long drawMapId);

    public List<DmDrawMap> findByFloorId(String floorId);

    @Override
    <S extends DmDrawMap> List<S> save(Iterable<S> iterable);

    @Modifying
    @Query("update DmDrawMap dm set dm.buildingId = ?1,dm.floorId = ?2,dm.dmTopic = ?3,dm.dwScale = ?4,dm.dwUnit = ?5,dm.height = ?6," +
            "dm.width = ?7,dm.picId = ?8,dm.dmNote = ?9,dm.status = ?10,dm.modTime='date ( \"Y/m/d H:i:s\" )' where dm.drawMapId =?11")
    public int updateDmDrawMap(Long buildingId, String floorId, String dmTopic, String dwScale, String dwUnit,
                               String height, String width, String picId, String dmNote, String status, Long drawMapId);

    @Query(value = "Select SEQ_INDOOR_DM_DRAW_MAP.nextval as id from dual",nativeQuery = true)
    public long getSeqId();

    /**
     * MT API
     * @param floorIds
     * @param statusA 正常->A
     * @return
     */
    public List<DmDrawMap> getAllByFloorIdInAndStatus(String floorIds, String statusA);
}
