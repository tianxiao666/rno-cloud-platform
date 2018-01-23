package com.hgicreate.rno.web.rest.gsm;


import com.hgicreate.rno.mapper.gsm.GsmFasAnalysisMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmFasAnalysisQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author yang.ch1
 */
@Slf4j
@RestController
@RequestMapping("/api/gsm-fas-analysis")
public class GsmFasAnalysisResource {

    private final GsmFasAnalysisMapper gsmFasAnalysisMapper;

    public GsmFasAnalysisResource(GsmFasAnalysisMapper gsmFasAnalysisMapper) {
        this.gsmFasAnalysisMapper = gsmFasAnalysisMapper;
    }

    @GetMapping("/fas-chart-query")
    public List<Map<String,Object>> queryFasChartData(GsmFasAnalysisQueryVM vm){
        log.debug("进入查询fas chart数据方法vm={}",vm);
        return gsmFasAnalysisMapper.queryFasChartData(
                vm.getFasMeaBegTime(),
                vm.getFasMeaEndTime(),
                "%"+vm.getCell()+"%");
    }
}
