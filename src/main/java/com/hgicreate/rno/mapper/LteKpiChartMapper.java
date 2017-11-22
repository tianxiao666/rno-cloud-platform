package com.hgicreate.rno.mapper;

import com.hgicreate.rno.service.dto.LteKpiChartCoverRateDTO;
import com.hgicreate.rno.service.dto.LteKpiChartRsrpDTO;
import com.hgicreate.rno.service.dto.LteKpiChartRsrqDTO;
import org.apache.ibatis.annotations.Mapper;


import java.util.Date;

@Mapper
public interface LteKpiChartMapper {

    LteKpiChartRsrpDTO queryChartRsrp(String areaId, String cellId,
                                      Date beginDate, Date endDate);

    LteKpiChartRsrqDTO queryChartRsrq(String areaId, String cellId,
                                      Date beginDate, Date endDate);

    LteKpiChartCoverRateDTO queryChartCoverRate(String areaId, String cellId,
                                                Date beginDate, Date endDate);
}
