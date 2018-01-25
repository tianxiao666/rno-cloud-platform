package com.hgicreate.rno.repository.indoor;


import com.hgicreate.rno.domain.indoor.CbFloor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbFloorRepository extends JpaRepository<CbFloor,Long>{

    @Override
    <S extends CbFloor> S save(S s);

    public List<CbFloor> findTop1000ByBuildingIdAndFloorNameContainingAndStatusContaining(Long buildingId, String floorName, String status);

    public void deleteByFloorId(Long floorId);

    @Modifying
    @Query("update CbFloor cf set cf.status = ?1 where cf.floorId =?2")
    public int updateFloorStatus(String status, Long floorId);

    public CbFloor findByFloorId(Long floorId);

    @Modifying
    @Query("update CbFloor cf set cf.floorName=?1,cf.floorType=?2,cf.physicalFloor=?3,cf.basement=?4,cf.floorNote=?5  where cf.floorId =?6")
    public int updateFloor(String floorName, String floorType, String physicalFloor, String basement, String floorNote, Long floorId);

    public List<CbFloor> findByBuildingId(Long buildingId);

    public List<CbFloor> findDistinctFloorIdByBuildingIdOrderByPhysicalFloorDesc(Long buildingId);

    public String getFloorNameByFloorId(Long floorId);

    /**
     * MT API
     * @param buildingId
     * @param statusX 失效->X
     * @return
     */
    public List<CbFloor> getAllByBuildingIdAndStatusNotContaining(Long buildingId, String statusX);
}
