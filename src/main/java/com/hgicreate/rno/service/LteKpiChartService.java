package com.hgicreate.rno.service;

import com.hgicreate.rno.mapper.LteKpiChartMapper;
import com.hgicreate.rno.service.dto.LteKpiChartCoverRateDTO;
import com.hgicreate.rno.service.dto.LteKpiChartRsrpDTO;
import com.hgicreate.rno.service.dto.LteKpiChartRsrqDTO;
import com.hgicreate.rno.web.rest.vm.LteKpiChartVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class LteKpiChartService {

    private final LteKpiChartMapper lteKpiChartMapper;

    public LteKpiChartService(LteKpiChartMapper lteKpiChartMapper) {
        this.lteKpiChartMapper = lteKpiChartMapper;
    }

    public ResponseEntity<?> queryChartData(LteKpiChartVM vm) throws ParseException {
        String cityId =vm.getCityId();
        String cellId = vm.getInputCell();
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate = sdf.parse(vm.getMrMeaBegDate());
        Date endDate = sdf2.parse(vm.getMrMeaEndDate()+" 23:59:59");
        switch (vm.getMrDataType()) {
            case "RSRP": {
                LteKpiChartRsrpDTO dto = lteKpiChartMapper.countChartRsrp(cityId, cellId, beginDate, endDate);
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
            case "RSRQ": {
                LteKpiChartRsrqDTO dto = lteKpiChartMapper.countChartRsrq(cityId, cellId, beginDate, endDate);
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
            default: {
                LteKpiChartCoverRateDTO dto = lteKpiChartMapper.countChartCoverRate(cityId, cellId, beginDate, endDate);
                return new ResponseEntity<Object>(dto, HttpStatus.OK);
            }
        }
    }
}
