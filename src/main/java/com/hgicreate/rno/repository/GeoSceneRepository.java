package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.GeoScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author ke_weixu
 */
@Repository
public interface GeoSceneRepository extends JpaRepository<GeoScene, Long> {
    /**
     * find geoScene by id
     * @param sceneId scene id
     * @return geo scene
     */
    GeoScene findSceneById(Long sceneId);

    /**
     * find all geoScenes
     * @return geoScenes
     */
    @Override
    List<GeoScene> findAll();

    /**
     * delete geoScene by id
     * @param sceneId scene id
     */
    void deleteById(Long sceneId);

    

}
