package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.TimeScene;
import com.hgicreate.rno.service.LteTimeSceneService;
import com.hgicreate.rno.service.dto.TimeSceneNameDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api/lte-time-scene")
public class LteTimeSceneResource {
    private final LteTimeSceneService lteTimeSceneService;

    public LteTimeSceneResource(LteTimeSceneService lteTimeSceneService) {
        this.lteTimeSceneService = lteTimeSceneService;
    }

    @PostMapping("/get-scene-by-id")
    public TimeScene getSceneById(Long sceneId){
        log.debug("查询的场景id为: " + sceneId);
        return lteTimeSceneService.getSceneById(sceneId);
    }

    @PostMapping("/get-all-name")
    public List<TimeSceneNameDTO> getAllName(){
        return lteTimeSceneService.getAllName();
    }

    @PostMapping("/delete-scene-by-id")
    public String deleteSceneById(Long sceneId){
        return lteTimeSceneService.deleteSceneById(sceneId);
    }

    @PostMapping("/insert-scene")
    public String insertScene(TimeScene sceneDataMap){
        return lteTimeSceneService.insertScene(sceneDataMap);
    }

    @PostMapping("/update-scene-by-id")
    public String updateScene(TimeScene timeScene){
        return lteTimeSceneService.updateScene(timeScene);
    }
}
