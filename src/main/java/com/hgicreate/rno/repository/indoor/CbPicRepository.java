package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.CbPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbPicRepository extends JpaRepository<CbPic,Long>{

    public String findDistinctPathByBuildingIdAndPathNotNull(String buildingId);

    public List<CbPic> findByPoiIdAndStatusNotContaining(String poiId, String status);

    public List<CbPic> findByPoiIdInAndStatusNotContaining(String poiId, String status);

    public String findDistinctPathByPath(String path);
}
