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

@Slf4j
@Service
@Transactional
public class LteTimeSceneService {
    private final TimeSceneRepository timeSceneRepository;

    public LteTimeSceneService(TimeSceneRepository timeSceneRepository) {
        this.timeSceneRepository = timeSceneRepository;
    }

    public TimeScene getSceneById(Long sceneId){
        return timeSceneRepository.findSceneById(sceneId);
    }

    public List<TimeSceneNameDTO> getAllName(){
       /* List<String> result = new ArrayList<>();*/
        //List<TimeScene> result = sceneRepository.findAll();TimeSceneNameMapper.INSTANCE::sceneToTimeSceneNameDTO
        List<TimeSceneNameDTO> result = timeSceneRepository.findAll().stream().map(TimeSceneNameMapper.INSTANCE::sceneToTimeSceneNameDTO).collect(Collectors.toList());
        /*for (TimeScene scene: list) {
            result.add(scene.getName());

        }*/

        return result;
    }

    public String deleteSceneById(Long sceneId){
        timeSceneRepository.deleteById(sceneId);
        return "success";
    }

    public String insertScene(TimeScene timeScene){
        timeSceneRepository.save(timeScene);
        return "success";
    }

    public String updateScene(TimeScene timeScene){
        timeSceneRepository.save(timeScene);
        return "success";
    }
}
