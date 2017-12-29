package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.repository.gsm.GsmDtAnalysisRepository;
import com.hgicreate.rno.service.gsm.dto.GsmDtDetailDTO;
import com.hgicreate.rno.mapper.gsm.GsmDtAnalysisDataMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmDtAnalysisMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmDtDetailMapper;
import com.hgicreate.rno.service.gsm.dto.GsmDtAnalysisDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

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
}
