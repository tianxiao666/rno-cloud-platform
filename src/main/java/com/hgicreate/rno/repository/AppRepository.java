package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {

    List<App> findAllByCode(String code);

}
