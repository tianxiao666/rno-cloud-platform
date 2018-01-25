package com.hgicreate.rno.repository.indoor;


import com.hgicreate.rno.domain.indoor.CbBuilding;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbBuildingRepository extends JpaRepository<CbBuilding, Long> {

    public List<CbBuilding> findTop1000ByBuildingNameContainingAndBuildTypeContaining(String buildingName, String buildType);

    public CbBuilding findByBuildingId(long buildingId);

    @Override
    <S extends CbBuilding> S save(S s);

    @Modifying
    @Query("update CbBuilding bu set bu.status = ?1 where bu.buildingId =?2")
    public int updateBuildingStatus(String status, Long buildingId);

    @Modifying
    @Query("update CbBuilding bu set bu.buildingName=?1,bu.area1.id=?2,bu.area.id=?3,bu.postalcode=?4,bu.address=?5,bu.buildType=?6,bu.totalFloor=?7," +
            "bu.phone=?8,bu.site=?9,bu.ltLongitudel=?10,bu.ltLatitudel=?11,bu.rbLongitudel=?12,bu.rbLatitudel=?13,bu.note=?14,bu.status = ?15,bu.district=?16 where bu.buildingId =?17")
    public int updateBuilding(String buildingName, Long prov, Long city, String postalcode, String address, String buildType, String totalFloor,
                              String phone, String site, Double ltLongitudel, Double ltLatitudel, Double rbLongitudel, Double rbLatitudel, String note, String status, String district, Long buildingId);

    public void deleteByBuildingId(Long buildingId);

    @Override
    List<CbBuilding> findAll(Sort sort);

    public String getBuildingNameByBuildingId(Long buildingId);
}
