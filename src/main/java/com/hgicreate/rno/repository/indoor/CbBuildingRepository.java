package com.hgicreate.rno.repository.indoor;


import com.hgicreate.rno.domain.indoor.CbBuilding;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface CbBuildingRepository extends JpaRepository<CbBuilding, Long> {

    /**
     *通过参数条件查找场所对象集合数据信息
     * @param buildingName
     * @param buildType
     * @return 符合条件的场所对象集合
     */
    public List<CbBuilding> findTop1000ByBuildingNameContainingAndBuildTypeContaining(String buildingName, String buildType);

    /**
     * 获取场所对象
     * @param buildingId
     * @return 场所对象
     */
    public CbBuilding findByBuildingId(long buildingId);

    /**
     * 保存场所数据
     * @param s
     * @param <S>
     * @return
     */
    @Override
    <S extends CbBuilding> S save(S s);

    /**
     * 更新建筑物状态
     * @param status
     * @param buildingId
     * @return 0 or 1
     */
    @Modifying
    @Query("update CbBuilding bu set bu.status = ?1 where bu.buildingId =?2")
    public int updateBuildingStatus(String status, Long buildingId);

    /**
     * 更新建筑物
     * @param buildingName
     * @param prov 省
     * @param city 市
     * @param postalcode 邮政
     * @param address 地址
     * @param buildType 建筑类型
     * @param totalFloor 总楼层
     * @param phone 电话
     * @param site 站点
     * @param ltLongitudel 左上经度
     * @param ltLatitudel 左上纬度
     * @param rbLongitudel 右下经度
     * @param rbLatitudel 右下纬度
     * @param note 提示
     * @param status 状态
     * @param district 地区
     * @param buildingId
     * @return 0 or 1
     */
    @Modifying
    @Query("update CbBuilding bu set bu.buildingName=?1,bu.area1.id=?2,bu.area.id=?3,bu.postalcode=?4,bu.address=?5,bu.buildType=?6,bu.totalFloor=?7," +
            "bu.phone=?8,bu.site=?9,bu.ltLongitudel=?10,bu.ltLatitudel=?11,bu.rbLongitudel=?12,bu.rbLatitudel=?13,bu.note=?14,bu.status = ?15,bu.district=?16 where bu.buildingId =?17")
    public int updateBuilding(String buildingName,Long prov,Long city,String postalcode,String address,String buildType,String totalFloor,
                              String phone,String site,Double ltLongitudel,Double ltLatitudel,Double rbLongitudel,Double rbLatitudel,String note,String status,String district,Long buildingId);

    /**
     * 通过ID删除建筑物
     * @param buildingId
     */
    public void deleteByBuildingId(Long buildingId);

    /**
     * 获取所有的建筑物
     * @param sort 升序
     * @return 建筑集合
     */
    @Override
    List<CbBuilding> findAll(Sort sort);

}
