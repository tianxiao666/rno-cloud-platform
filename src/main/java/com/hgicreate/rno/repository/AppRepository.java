package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ke_weixu
 */
@Repository
public interface AppRepository extends JpaRepository<App, Long> {

    /**
     * find all apps by code
     * @param code code
     * @return Apps
     */
    List<App> findAllByCode(String code);
}
