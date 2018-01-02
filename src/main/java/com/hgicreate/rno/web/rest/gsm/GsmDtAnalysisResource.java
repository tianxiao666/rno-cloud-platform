package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.repository.gsm.GsmDtAnalysisRepository;
import com.hgicreate.rno.service.gsm.dto.GsmDtDetailDTO;
import com.hgicreate.rno.mapper.gsm.GsmDtAnalysisDataMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmDtAnalysisMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmDtDetailMapper;
import com.hgicreate.rno.service.gsm.dto.GsmDtAnalysisDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/gsm-dt-analysis")
public class GsmDtAnalysisResource {

    private final GsmDtAnalysisRepository gsmDtAnalysisRepository;
    private final GsmDtAnalysisDataMapper gsmDtAnalysisDataMapper;

    public GsmDtAnalysisResource(GsmDtAnalysisRepository gsmDtAnalysisRepository,GsmDtAnalysisDataMapper gsmDtAnalysisDataMapper) {
        this.gsmDtAnalysisRepository = gsmDtAnalysisRepository;
        this.gsmDtAnalysisDataMapper = gsmDtAnalysisDataMapper;
    }

    @GetMapping("/dt-data")
    public List<GsmDtAnalysisDTO> queryDtData(String descId) {
        log.debug("区域id为: " + descId);
        return gsmDtAnalysisRepository.findAllByGsmDtDesc_AreaIdOrderBySampleTime(Long.parseLong(descId))
                .stream()
                .map(GsmDtAnalysisMapper.INSTANCE::gsmDtAnalysisToGsmDtAnalysisDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/dt-data-detail")
    public List<GsmDtDetailDTO> queryDtDetail(String dataId) {
        log.debug("查询id为: " + dataId);
        return gsmDtAnalysisRepository.findAllById(Long.parseLong(dataId))
                .stream()
                .map(GsmDtDetailMapper.INSTANCE::gsmDtDetailToGsmDtDetailDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/get-cell")
    public List<Map<String, Object>> queryCell() {
        log.debug("查询主小区");
        return gsmDtAnalysisDataMapper.getDistinctCell();
    }

    @GetMapping("/get-ncell")
    public List<Map<String, Object>> queryNcell(String longitude,String latitude) {
        log.debug("查询邻区={}",longitude,latitude);
        return gsmDtAnalysisDataMapper.getNcell(Double.parseDouble(longitude),Double.parseDouble(latitude));
    }

    @GetMapping("/cell-coverage")
    public List<Map<String, Object>> queryCellCoverage(String cellId) {
        log.debug("查询小区覆盖");
        return gsmDtAnalysisDataMapper.getCoverageCell(cellId);
    }

    @GetMapping("/sample-coverage")
    public List<Map<String, Object>> querySampleCoverage(String cellId) {
        log.debug("查询采样点小区覆盖");
        return gsmDtAnalysisDataMapper.getCoverageSample(cellId);
    }

    @GetMapping("/get-weak-cover")
    public List<Map<String, Object>> queryWeakCoverageCell() {
        log.debug("查询弱覆盖小区");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }
    @GetMapping("/get-over-cover")
    public List<Map<String, Object>> queryOverCoverageCell() {
        log.debug("查询覆盖过远小区");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }
    @GetMapping("/get-indoor-signal")
    public List<Map<String, Object>> queryIndoorSignalLeakOutsideCell() {
        log.debug("查询室分外泄小区");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }
    @GetMapping("/get-no-main-cell")
    public List<Map<String, Object>> queryNoMainCoverageCell() {
        log.debug("查询无主覆盖小区");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }
    @GetMapping("/get-rapid-atten-cell")
    public List<Map<String, Object>> queryRapidAttenuationCell() {
        log.debug("查询信号快速衰减小区");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }
    @GetMapping("/get-signal-antenna-cell")
    public List<Map<String, Object>> querySignalAndAntennaNotMatchCell() {
        log.debug("查询信号与天线方向不符小区");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        return list;
    }
}
