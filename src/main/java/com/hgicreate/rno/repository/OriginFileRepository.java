package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.OriginFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OriginFileRepository extends JpaRepository<OriginFile,Long> {
}
