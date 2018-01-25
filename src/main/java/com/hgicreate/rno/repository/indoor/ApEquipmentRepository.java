package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.ApEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApEquipmentRepository extends JpaRepository<ApEquipment,Long> {

    @Override
    <S extends ApEquipment> S save(S s);

    public List<ApEquipment> findTop1000ByBuildingIdAndEqutSsidContainingAndFloorIdContainingAndEqutTypeContainingAndStatusContaining(String buildingId, String equtSsid,
                                                                                                                                      String floorId, String equtType, String status);

    public void deleteByApId(Long apId);

    @Modifying
    @Query("update ApEquipment ap set ap.status = ?1 where ap.apId =?2")
    public int updateApStatus(String status, Long apId);

    public ApEquipment findByApId(Long apId);

    public List<ApEquipment> findByDrawMapId(String drawMapId);

    public int deleteByDrawMapId(String drawMapId);

    @Override
    <S extends ApEquipment> List<S> save(Iterable<S> iterable);

    @Query(value = "Select SEQ_INDOOR_AP_EQUIPMENT.nextval as id from dual",nativeQuery = true)
    public long getSeqId();
}
