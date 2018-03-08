package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.IdealApMeaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chao.xj
 */
@Repository
public interface IdealApMeaDataRepository extends JpaRepository<IdealApMeaData,Long>  {

    /**
     * 保存理想AP采集数据
     * @param s
     * @param <S>
     * @return
     */
    @Override
    <S extends IdealApMeaData> S save(S s);
}
