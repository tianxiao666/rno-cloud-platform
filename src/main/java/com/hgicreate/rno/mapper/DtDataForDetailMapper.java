package com.hgicreate.rno.mapper;

import com.hgicreate.rno.domain.DtDataForDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DtDataForDetailMapper {

    List<DtDataForDetail> findDtCellDetailById(Long id);
}
