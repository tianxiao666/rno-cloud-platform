package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.OriginFileAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OriginFileAttrRepository extends JpaRepository<OriginFileAttr,Long> {
    @Query(value = "SELECT ORIGIN_FILE_ID FROM RNO_ORIGIN_FILE_ATTR WHERE ORIGIN_FILE_ID=(SELECT Max(ORIGIN_FILE_ID) FROM RNO_ORIGIN_FILE_ATTR)", nativeQuery = true)
    Integer getOriginFileAttrNum();
}
