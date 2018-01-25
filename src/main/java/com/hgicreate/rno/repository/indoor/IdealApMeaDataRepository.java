package com.hgicreate.rno.repository.indoor;

import com.hgicreate.rno.domain.indoor.IdealApMeaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdealApMeaDataRepository extends JpaRepository<IdealApMeaData,Long>  {

    @Override
    <S extends IdealApMeaData> S save(S s);
}
