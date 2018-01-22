package com.hgicreate.rno.repository.gsm;

import com.hgicreate.rno.domain.gsm.GsmStructJobParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tao.xj
 */
@Repository
public interface GsmStructJobParamRepository extends JpaRepository<GsmStructJobParam,Long>{
}
