package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.GeoScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GeoSceneRepository extends JpaRepository<GeoScene, Long> {
    GeoScene findSceneById(Long sceneId);
    List<GeoScene> findAll();

    void deleteById(Long sceneId);

    

}
