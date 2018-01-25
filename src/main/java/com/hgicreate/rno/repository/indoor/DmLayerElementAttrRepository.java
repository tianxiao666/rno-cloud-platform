package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmLayerElementAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DmLayerElementAttrRepository extends JpaRepository<DmLayerElementAttr,Long>{

    public List<DmLayerElementAttr> findByDrawMapId(String drawMapId);

    public int deleteByDrawMapId(String drawMapId);

    @Override
    <S extends DmLayerElementAttr> List<S> save(Iterable<S> iterable);
}
