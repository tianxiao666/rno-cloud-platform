package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.TypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chao.xj
 */
@Repository
public interface TypeCodeRepository extends JpaRepository<TypeCode,Long> {

    /**
     * 获取类型编码集合数据
     * @return 类型编码集合数据
     */
    @Override
    List<TypeCode> findAll();
}
