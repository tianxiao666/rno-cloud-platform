package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.DmLayerElementAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface DmLayerElementAttrRepository extends JpaRepository<DmLayerElementAttr,Long>{

    /**
     * 获取图层元素属性集合
     * @param drawMapId
     * @return 图层元素属性集合
     */
    public List<DmLayerElementAttr> findByDrawMapId(String drawMapId);

    /**
     * 删除图层元素属性
     * @param drawMapId
     * @return 1 成功 or 0 失败
     */
    public int deleteByDrawMapId(String drawMapId);

    /**
     * 批保存图层元素属性集合
     * @param iterable
     * @param <S>
     * @return
     */
    @Override
    <S extends DmLayerElementAttr> List<S> save(Iterable<S> iterable);
}
