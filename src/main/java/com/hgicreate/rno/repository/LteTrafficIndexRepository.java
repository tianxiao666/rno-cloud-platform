package com.hgicreate.rno.repository;

import com.hgicreate.rno.domain.LteTrafficIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LteTrafficIndexRepository extends JpaRepository<LteTrafficIndex,Integer>{
}
