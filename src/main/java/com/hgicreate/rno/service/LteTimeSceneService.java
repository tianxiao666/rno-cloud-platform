package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.TimeScene;
import com.hgicreate.rno.repository.TimeSceneRepository;
import com.hgicreate.rno.service.dto.TimeSceneNameDTO;
import com.hgicreate.rno.service.mapper.TimeSceneNameMapper;
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
public class LteTimeSceneService {
    private final TimeSceneRepository timeSceneRepository;

    public LteTimeSceneService(TimeSceneRepository timeSceneRepository) {
        this.timeSceneRepository = timeSceneRepository;
    }

    public TimeScene getSceneById(Long sceneId){
        return timeSceneRepository.findSceneById(sceneId);
    }

    public List<TimeSceneNameDTO> getAllName(){
        return timeSceneRepository.findAll().stream().map(TimeSceneNameMapper.INSTANCE::sceneToTimeSceneNameDTO).collect(Collectors.toList());
    }

    public void deleteSceneById(Long sceneId){
        timeSceneRepository.deleteById(sceneId);
    }

    public void insertScene(TimeScene timeScene){
        timeSceneRepository.save(timeScene);
    }

    public void updateScene(TimeScene timeScene){
        timeSceneRepository.save(timeScene);
    }
}
