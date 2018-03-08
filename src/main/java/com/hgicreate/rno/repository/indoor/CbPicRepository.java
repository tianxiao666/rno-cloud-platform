package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.CbPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface CbPicRepository extends JpaRepository<CbPic,Long>{

    /**
     *获取符合条件图片路径
     * @param buildingId
     * @return 图片路径
     */
    public String findDistinctPathByBuildingIdAndPathNotNull(String buildingId);

    /**
     * 通过ID和状态获取图片数据
     * @param poiId
     * @param status
     * @return 图片数据集合
     */
    public List<CbPic> findByPoiIdInAndStatusNotContaining(String poiId,String status);

    /**
     * 查找路径
     * @param path
     * @return 路径
     */
    public String findDistinctPathByPath(String path);
}
