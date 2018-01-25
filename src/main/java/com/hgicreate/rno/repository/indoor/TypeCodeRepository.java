package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.TypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeCodeRepository extends JpaRepository<TypeCode,Long> {

    List<TypeCode> findAll();
}
