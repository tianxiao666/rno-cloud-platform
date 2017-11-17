package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.TimeScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TimeSceneRepository extends JpaRepository<TimeScene, Long> {
    TimeScene findSceneById(Long sceneId);
    List<TimeScene> findAll();

    void deleteById(Long sceneId);

    

}
