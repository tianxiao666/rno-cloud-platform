package com.hgicreate.rno.repository.indoor;


import com.hgicreate.rno.domain.indoor.MtSignalMeaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MtSignalMeaDataRepository extends JpaRepository<MtSignalMeaData,Long>{

    @Override
    <S extends MtSignalMeaData> S save(S s);
}
