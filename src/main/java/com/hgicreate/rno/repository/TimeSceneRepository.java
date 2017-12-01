package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.TimeScene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author ke_weixu
 */
@Repository
public interface TimeSceneRepository extends JpaRepository<TimeScene, Long> {
}
