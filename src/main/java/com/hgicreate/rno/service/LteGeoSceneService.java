package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.GeoScene;
import com.hgicreate.rno.repository.GeoSceneRepository;
import com.hgicreate.rno.service.dto.GeoSceneNameDTO;
import com.hgicreate.rno.service.mapper.GeoSceneNameMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LteGeoSceneService {
    private final GeoSceneRepository geoSceneRepository;

    public LteGeoSceneService(GeoSceneRepository geoSceneRepository) {
        this.geoSceneRepository = geoSceneRepository;
    }

    public GeoScene getSceneById(Long sceneId){
        return geoSceneRepository.findSceneById(sceneId);
    }

    public List<GeoSceneNameDTO> getAllName(){
       /* List<String> result = new ArrayList<>();*/
        //List<GeoScene> result = sceneRepository.findAll();
        List<GeoSceneNameDTO> result = geoSceneRepository.findAll().stream().map(GeoSceneNameMapper.INSTANCE::sceneToGeoSceneNameDTO).collect(Collectors.toList());
        /*for (GeoScene scene: list) {
            result.add(scene.getName());

        }*/

        return result;
    }

    public String deleteSceneById(Long sceneId){
        geoSceneRepository.deleteById(sceneId);
        return "success";
    }

    public String insertScene(GeoScene geoScene){
        geoSceneRepository.save(geoScene);
        return "success";
    }

    public String updateScene(GeoScene geoScene){
        geoSceneRepository.save(geoScene);
        return "success";
    }
}
