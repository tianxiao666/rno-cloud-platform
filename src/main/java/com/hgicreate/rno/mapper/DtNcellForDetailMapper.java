package com.hgicreate.rno.mapper;

import com.hgicreate.rno.domain.DtNcellForDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DtNcellForDetailMapper {

    List<DtNcellForDetail> findDtNCellDetailById(Long id);
}
