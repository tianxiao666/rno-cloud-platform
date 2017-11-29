package com.hgicreate.rno.mapper;

import com.hgicreate.rno.service.dto.LteKpiChartCoverRateDTO;
import com.hgicreate.rno.service.dto.LteKpiChartRsrpDTO;
import com.hgicreate.rno.service.dto.LteKpiChartRsrqDTO;
import org.apache.ibatis.annotations.Mapper;


import java.util.Date;

@Mapper
public interface LteKpiChartMapper {

    LteKpiChartRsrpDTO countChartRsrp(String areaId, String cellId,
                                      Date beginDate, Date endDate);

    LteKpiChartRsrqDTO countChartRsrq(String areaId, String cellId,
                                      Date beginDate, Date endDate);

    LteKpiChartCoverRateDTO countChartCoverRate(String areaId, String cellId,
                                                Date beginDate, Date endDate);
}
