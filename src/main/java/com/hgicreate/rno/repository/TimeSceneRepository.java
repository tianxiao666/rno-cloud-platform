package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.TimeScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author ke_weixu
 */
@Repository
public interface TimeSceneRepository extends JpaRepository<TimeScene, Long> {
    /**
     * find timeScene by id
     * @param sceneId scene id
     * @return time scene
     */
    TimeScene findSceneById(Long sceneId);

    /**
     * find all timeScenes
     * @return timeScenes
     */
    @Override
    List<TimeScene> findAll();

    /**
     * delete timeScene by id
     * @param sceneId scene id
     */
    void deleteById(Long sceneId);

    

}
