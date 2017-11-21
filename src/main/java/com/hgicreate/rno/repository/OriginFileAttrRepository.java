package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.OriginFileAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OriginFileAttrRepository extends JpaRepository<OriginFileAttr,Long> {
}
