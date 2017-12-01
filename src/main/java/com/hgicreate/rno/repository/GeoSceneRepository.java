package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.GeoScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author ke_weixu
 */
@Repository
public interface GeoSceneRepository extends JpaRepository<GeoScene, Long> {
}
