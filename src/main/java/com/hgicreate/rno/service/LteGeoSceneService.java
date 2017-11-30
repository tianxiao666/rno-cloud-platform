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

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class LteGeoSceneService {
    private final GeoSceneRepository geoSceneRepository;

    public LteGeoSceneService(GeoSceneRepository geoSceneRepository) {
        this.geoSceneRepository = geoSceneRepository;
    }

    public GeoScene getSceneById(Long sceneId){
        return geoSceneRepository.findSceneById(sceneId);
    }

    public List<GeoSceneNameDTO> getAllName(){
        return geoSceneRepository.findAll().stream().map(GeoSceneNameMapper.INSTANCE::sceneToGeoSceneNameDTO).collect(Collectors.toList());
    }

    public void deleteSceneById(Long sceneId){
        geoSceneRepository.deleteById(sceneId);
    }

    public void insertScene(GeoScene geoScene){
        geoSceneRepository.save(geoScene);
    }

    public void updateScene(GeoScene geoScene){
        geoSceneRepository.save(geoScene);
    }
}
