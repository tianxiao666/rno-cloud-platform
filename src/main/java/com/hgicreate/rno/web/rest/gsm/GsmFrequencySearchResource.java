package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.mapper.gsm.GsmFrequencySearchMapper;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.web.rest.gsm.vm.GsmFrequencyUpdateVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gsm-frequency-search")
public class GsmFrequencySearchResource {

    private final GsmCellDataRepository gsmCellDataRepository;

    private final GsmFrequencySearchMapper gsmFrequencySearchMapper;

    public GsmFrequencySearchResource(GsmCellDataRepository gsmCellDataRepository, GsmFrequencySearchMapper gsmFrequencySearchMapper) {
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.gsmFrequencySearchMapper = gsmFrequencySearchMapper;
    }

    @PostMapping("/update-cell-freq-by-cellId")
    public boolean updateCellFreqByCellId(GsmFrequencyUpdateVM vm){
        log.debug("更新频点参数bcch={},tch={},cellId={}",
                vm.getBcch(),vm.getTch(),vm.getCellId());
        GsmCell gsmCell = gsmCellDataRepository.findOne(vm.getCellId());
        gsmCell.setBcch(Integer.parseInt(vm.getBcch()));
        gsmCell.setTch(vm.getTch());
        gsmCellDataRepository.save(gsmCell);
        return true;
    }

    @GetMapping("/cell-search")
    public List<GsmCell> searchCellDetail(@RequestParam String conditionType,
                                          @RequestParam String conditionValue,
                                          @RequestParam String cityId){
        log.debug("进入搜小区方法conditionType={},conditionValue={}",conditionType,conditionValue);
        String cellId = null,cellName = null,cellEnName = null,lac = null,ci = null;
        switch (conditionType){
            case "cellId":
                cellId = conditionValue;
                break;
            case "cellName":
                cellName = "%" + conditionValue + "%";
                break;
            case "cellEnName":
                cellEnName = conditionValue;
                break;
            case "las":
                lac = conditionValue;
                break;
            default:
                ci= conditionValue;
        }
        return gsmFrequencySearchMapper.findCellByCondition(cellId,cellName,cellEnName,lac,ci,cityId);
    }

    @GetMapping("/ncell-search")
    public List<GsmCell> searchNcellDetail(String cellForNcell){
        log.debug("进入搜邻区方法cellId={}",cellForNcell);
        return gsmFrequencySearchMapper.findNcellByCondition(cellForNcell);
    }

    @GetMapping("/frequency-search")
    public List<GsmCell> searchFrequency(String bcch,String cityId){
        log.debug("进入搜频点方法bcch={}",bcch);
        return gsmFrequencySearchMapper.findCellByBcchAndCityId(bcch,cityId);
    }

    @GetMapping("/cell-noise")
    public List<Map<String,Object>> getCellNoise(String cellId){
        log.debug("进入查看Noise方法cellId={}",cellId);
        return null;
    }

    @GetMapping("/cell-in")
    public List<Map<String,Object>> getCellIn(String cellId){
        log.debug("进入in干扰方法cellId={}",cellId);
        return null;
    }

    @GetMapping("/cell-out")
    public List<Map<String,Object>> getCellOut(String cellId){
        log.debug("进入out干扰方法cellId={}",cellId);
        return null;
    }
}
