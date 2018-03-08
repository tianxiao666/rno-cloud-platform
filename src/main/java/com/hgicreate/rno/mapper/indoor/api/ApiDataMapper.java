package com.hgicreate.rno.mapper.indoor.api;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chao.xj
 */
@Mapper
public interface ApiDataMapper {

    /**
     * 查询该场所ID的所有楼层的信息,并匹配楼层和其对应的楼层平面图ID
     * @param buildingId 楼层ID
     * @return 匹配楼层和其对应的楼层平面图ID
     */
    public List<Map<String,Object>> getBuildingFloorList(@Param("buildingId") long buildingId);

    /**
     * 获取移动终端栅格测量数据
     * @param map 栅格参数条件
     * @return 栅格测量数据集合
     */
    public List<Map<String,Object>> getMtSignalGridMeaData(Map<String,Object> map);

    /**
     * 保存移动终端信号强度相关信息
     * @param map 移动信号对象参数
     */
    public void saveMtSignalData(Map<String,Object> map);

    /**
     * 保存理想ap信号强度相关信息
     * @param map AP对象参数
     */
    public void saveIdealApData(Map<String,Object> map);

    /**
     * 按条件查询所有POI数据
     * @param map poi对象参数
     * @return 符合条件所有POI数据
     */
    public List<Map<String,Object>> getPoiList(Map<String,Object> map);

    /**
     * 获取附近场所数据
     * @param map 符合条件参数
     * @return 附近场所数据
     */
    public List<Map<String,Object>> getNearbyBuildingList(Map<String,Object> map);

    /**
     * 取图片信息
     * @param picWhere
     * @return 图片信息集合
     */
    public List<Map<String,Object>> getPic(@Param("picWhere") String picWhere);

    /**
     * 获取Ap定位数据信息
     * @param map AP对象参数
     * @return Ap定位数据信息集合
     */
    public List<Map<String,Object>> getApList(Map<String,Object> map);

    /**
     * 定位的场所数据
     * @param map 场所条件参数
     * @return 定位的场所数据集合
     */
    public List<Map<String,Object>> getBuildingList(Map<String,Object> map);

    /**
     * 通过ID获取对应场所名
     * @param buildingId
     * @return 场所名
     */
    public String getBuildingNameById(@Param("buildingId") long buildingId);

    /**
     * 获取Ap定位接入点信息
     * @param map 定位参数
     * @return Ap定位接入点信息集合
     */
    public List<Map<String,Object>> getApLocationList(Map<String,Object> map);

    /**
     * 通过ID获取对应楼层名
     * @param floorId
     * @return 楼层名
     */
    public String getFloorNameById(@Param("floorId") long floorId);

    /**
     * 通过floorID获取对应楼层集合
     * @param floorId
     * @return 楼层集合数据
     */
    public List<Map<String,Object>> getFloorListById(@Param("floorId") long floorId);

    /**
     * 通过buildingID获取对应楼层集合
     * @param buildingId
     * @return 楼层集合
     */
    public List<Map<String,Object>> getFloorListByBuildingId(long buildingId);

    /**
     * 获取附近匹配场所接口
     * @param map 场所参数
     * @return 附近匹配场所集合
     */
    public List<Map<String,Object>> getMatchBuildingList(Map<String,Object> map);

    /**
     * 通过macbssids获取AP信息集合
     * @param map macbssid参数
     * @return AP信息集合
     */
    public List<Map<String,Object>> getApListByMacbssids(Map<String,Object> map);
}
