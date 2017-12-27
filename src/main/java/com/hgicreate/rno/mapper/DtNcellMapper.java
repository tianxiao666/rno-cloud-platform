package com.hgicreate.rno.mapper;

import com.hgicreate.rno.domain.DtNcell;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DtNcellMapper {

    List<DtNcell> findByDataIdInOrderByDataIdAsc(Long id);
}
