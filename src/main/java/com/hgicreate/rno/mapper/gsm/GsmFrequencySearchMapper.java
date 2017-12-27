package com.hgicreate.rno.mapper.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GsmFrequencySearchMapper {

    List<GsmCell> findCellByCondition(@Param("cellId") String cellId,
                                             @Param("cellName") String cellName,
                                             @Param("cellEnName") String cellEnName,
                                             @Param("lac") String lac,
                                             @Param("ci") String ci);

    List<GsmCell> findNcellByCondition(@Param("cellId") String cellId);

    List<GsmCell> findCellByBcchAndCityId(@Param("bcch") String bcch,@Param("cityId") String cityId);
}
