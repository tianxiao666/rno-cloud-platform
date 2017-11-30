package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.GeoScene;
import com.hgicreate.rno.service.LteGeoSceneService;
import com.hgicreate.rno.service.dto.GeoSceneNameDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api/lte-geo-scene")
public class LteGeoSceneResource {
    private final LteGeoSceneService lteGeoSceneService;

    public LteGeoSceneResource(LteGeoSceneService lteGeoSceneService) {
        this.lteGeoSceneService = lteGeoSceneService;
    }

    @PostMapping("/get-scene-by-id")
    public GeoScene getSceneById(Long sceneId){
        log.debug("查询的场景id为: " + sceneId);
        return lteGeoSceneService.getSceneById(sceneId);
    }

    @PostMapping("/get-all-name")
    public List<GeoSceneNameDTO> getAllName(){
        return lteGeoSceneService.getAllName();
    }

    @PostMapping("/delete-scene-by-id")
    public void deleteSceneById(Long sceneId){
        lteGeoSceneService.deleteSceneById(sceneId);
    }

    @PostMapping("/insert-scene")
    public void insertScene(GeoScene sceneDataMap){
        lteGeoSceneService.insertScene(sceneDataMap);
    }

    @PostMapping("/update-scene-by-id")
    public void updateScene(GeoScene geoScene){
        lteGeoSceneService.updateScene(geoScene);
    }
}
