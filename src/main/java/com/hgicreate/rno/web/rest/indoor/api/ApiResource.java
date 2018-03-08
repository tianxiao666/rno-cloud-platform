package com.hgicreate.rno.web.rest.indoor.api;

import com.hgicreate.rno.service.indoor.api.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chao.xj
 */
@Slf4j
@RestController
@RequestMapping("/api/mt-interface-data")
public class ApiResource {

    private final ApiService apiService;

    public ApiResource(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/GetBuildingFloorList")
    public String getBuildingFloorList(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getBuildingFloorList 获取楼层列表数据,jsonArrayObj={}",jsonArrayObj);
        return apiService.getBuildingFloorList(jsonArrayObj);
    }

    @GetMapping("/GetBuildingFloorMap")
    public String getBuildingFloorMap(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getBuildingFloorMap 获取室内地图数据,jsonArrayObj={}",jsonArrayObj);
        return apiService.getBuildingFloorMap(jsonArrayObj);
    }

    @GetMapping("/GetBuildingTypeList")
    public String getBuildingTypeList(){
        log.debug("进入资源 接口getBuildingTypeList 获取场所类型数据!");
        return apiService.getBuildingTypeList();
    }

    @GetMapping("/GetFloorTypeList")
    public String getFloorTypeList(){
        log.debug("进入资源 接口getFloorTypeList 获取楼层类型数据!");
        return apiService.getFloorTypeList();
    }

    @GetMapping("/GetMtSignalList")
    public String getMtSignalList(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getMtSignalList 获取移动终端信号坐标栅格周期集合数据接口,jsonArrayObj={}",jsonArrayObj);
        return apiService.getMtSignalList(jsonArrayObj);
    }

    @GetMapping("/MtSignalCollection")
    public String mtSignalCollection(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口mtSignalCollection 移动终端信号强度测量数据采集接口,jsonArrayObj={}",jsonArrayObj);
        return apiService.mtSignalCollection(jsonArrayObj);
    }

    @GetMapping("/IdealApCollection")
    public String idealApCollection(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口idealApCollection 理想AP测量数据采集接口,jsonArrayObj={}",jsonArrayObj);
        return apiService.idealApCollection(jsonArrayObj);
    }

    @GetMapping("/GetPoiList")
    public String getPoiList(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getPoiList 获取poi信息列表,jsonArrayObj={}",jsonArrayObj);
        return apiService.getPoiList(jsonArrayObj);
    }

    @GetMapping("/GetNearbyBuildingList")
    public String getNearbyBuildingList(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getNearbyBuildingList 获取附近场所接口,jsonArrayObj={}",jsonArrayObj);
        return apiService.getNearbyBuildingList(jsonArrayObj);
    }

    @GetMapping("/CheckNewVersion")
    public String checkNewVersion(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口checkNewVersion 获取新版本接口,jsonArrayObj={}",jsonArrayObj);
        return apiService.checkNewVersion(jsonArrayObj);
    }

    @GetMapping("/GetApList")
    public String getApList(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getApList 获取ap信息列表,jsonArrayObj={}",jsonArrayObj);
        return apiService.getApList(jsonArrayObj);
    }

    @GetMapping("/GetLocation")
    public String getLocation(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getLocation 定位接口,jsonArrayObj={}",jsonArrayObj);
        return apiService.getLocation(jsonArrayObj);
    }

    @GetMapping("/getMatchBuildingList")
    public String getMatchBuildingList(@RequestParam("jsonArrayObj") String jsonArrayObj){
        log.debug("进入资源 接口getMatchBuildingList 获取附近匹配场所接口,jsonArrayObj={}",jsonArrayObj);
        return apiService.getMatchBuildingList(jsonArrayObj);
    }
}
