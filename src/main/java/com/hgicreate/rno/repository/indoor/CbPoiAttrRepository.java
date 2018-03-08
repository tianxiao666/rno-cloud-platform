package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.CbPoiAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chao.xj
 */
@Repository
public interface CbPoiAttrRepository extends JpaRepository<CbPoiAttr,Long> {

}
