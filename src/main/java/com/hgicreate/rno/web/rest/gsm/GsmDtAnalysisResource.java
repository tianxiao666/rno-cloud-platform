package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.repository.gsm.GsmDtAnalysisRepository;
import com.hgicreate.rno.repository.gsm.GsmDtDetailRepository;
import com.hgicreate.rno.service.gsm.dto.GsmDtDetailDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmDtAnalysisMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmDtDetailMapper;
import com.hgicreate.rno.service.gsm.dto.GsmDtAnalysisDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/gsm-dt-analysis")
public class GsmDtAnalysisResource {

    private final GsmDtAnalysisRepository gsmDtAnalysisRepository;
    private final GsmDtDetailRepository gsmDtDetailRepository;

    public GsmDtAnalysisResource(GsmDtAnalysisRepository gsmDtAnalysisRepository, GsmDtDetailRepository gsmDtDetailRepository) {
        this.gsmDtAnalysisRepository = gsmDtAnalysisRepository;
        this.gsmDtDetailRepository = gsmDtDetailRepository;
    }

    @GetMapping("/dt-data")
    public List<GsmDtAnalysisDTO> queryDtData(String descId) {
        log.debug("区域id为: " + descId);
        List<GsmDtAnalysisDTO> list = gsmDtAnalysisRepository.findAllByGsmDtDesc_AreaId(Long.parseLong(descId))
                .stream()
                .map(GsmDtAnalysisMapper.INSTANCE::gsmDtAnalysisToGsmDtAnalysisDTO)
                .collect(Collectors.toList());
        log.debug("aaaaaaaaa-------"+list);
        return gsmDtAnalysisRepository.findAllByGsmDtDesc_AreaId(Long.parseLong(descId))
                .stream()
                .map(GsmDtAnalysisMapper.INSTANCE::gsmDtAnalysisToGsmDtAnalysisDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/dt-data-detail")
    public List<GsmDtDetailDTO> queryDtDetail(String dataId) {
        log.debug("查询id为: " + dataId);
        return gsmDtDetailRepository.findAllByGsmDtData_Id(Long.parseLong(dataId))
                .stream()
                .map(GsmDtDetailMapper.INSTANCE::gsmDtDetailToGsmDtDetailDTO)
                .collect(Collectors.toList());
    }
}
