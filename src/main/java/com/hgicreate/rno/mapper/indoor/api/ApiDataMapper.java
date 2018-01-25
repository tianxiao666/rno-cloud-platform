package com.hgicreate.rno.mapper.indoor.api;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApiDataMapper {

    /**
     * 查询该场所ID的所有楼层的信息,并匹配楼层和其对应的楼层平面图ID
     */
    public List<Map<String,Object>> getBuildingFloorList(@Param("buildingId") long buildingId);

    /**
     * 获取移动终端栅格测量数据
     */
    public List<Map<String,Object>> getMtSignalGridMeaData(Map<String, Object> map);

    /**
     * 保存移动终端信号强度相关信息
     */
    public void saveMtSignalData(Map<String, Object> map);

    /**
     * 保存理想ap信号强度相关信息
     */
    public void saveIdealApData(Map<String, Object> map);

    /**
     * 按条件查询所有POI数据
     */
    public List<Map<String,Object>> getPoiList(Map<String, Object> map);

    /**
     * 获取附近场所数据
     */
    public List<Map<String,Object>> getNearbyBuildingList(Map<String, Object> map);

    /**
     * 取图片信息
     */
    public List<Map<String,Object>> getPic(@Param("picWhere") String picWhere);

    /**
     * 获取Ap定位数据信息
     */
    public List<Map<String,Object>> getApList(Map<String, Object> map);

    /**
     * 定位的场所数据
     */
    public List<Map<String,Object>> getBuildingList(Map<String, Object> map);

    /**
     * 通过ID获取对应场所名
     */
    public String getBuildingNameById(@Param("buildingId") long buildingId);

    /**
     * 获取Ap定位接入点信息
     */
    public List<Map<String,Object>> getApLocationList(Map<String, Object> map);

    /**
     * 通过ID获取对应楼层名
     */
    public String getFloorNameById(@Param("floorId") long floorId);

    /**
     * 通过floorID获取对应楼层集合
     */
    public List<Map<String,Object>> getFloorListById(@Param("floorId") long floorId);

    /**
     * 通过buildingID获取对应楼层集合
     */
    public List<Map<String,Object>> getFloorListByBuildingId(long buildingId);

    /**
     * 获取附近匹配场所接口
     */
    public List<Map<String,Object>> getMatchBuildingList(Map<String, Object> map);

    /**
     * 通过macbssids获取AP信息集合
     */
    public List<Map<String,Object>> getApListByMacbssids(Map<String, Object> map);
}
