package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    public List<Area> findAllByParentId(long parentId);

}
