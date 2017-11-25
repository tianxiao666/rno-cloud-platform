package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.LteTrafficIndex;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.LteTrafficIndexRepository;
import com.hgicreate.rno.service.LteKpiQueryService;
import com.hgicreate.rno.service.dto.LteTrafficIndexDTO;
import com.hgicreate.rno.service.mapper.LteTrafficIndexMapper;
import com.hgicreate.rno.web.rest.vm.LteKpiQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/lte-kpi-query")
public class LteKpiQueryResource {

    private final LteTrafficIndexRepository lteTrafficIndexRepository;

    private final LteKpiQueryService lteKpiQueryService;
    private final AreaRepository areaRepository;

    public LteKpiQueryResource(LteTrafficIndexRepository lteTrafficIndexRepository, LteKpiQueryService lteKpiQueryService, AreaRepository areaRepository) {
        this.lteTrafficIndexRepository = lteTrafficIndexRepository;
        this.lteKpiQueryService = lteKpiQueryService;
        this.areaRepository = areaRepository;
    }

    @PostMapping("/load-index")
    public List<LteTrafficIndexDTO> getRno4GIndex() {
        log.debug("获取话统指标");
        List<LteTrafficIndex> list = lteTrafficIndexRepository.findAll();
        return list.stream()
                .map(LteTrafficIndexMapper.INSTANCE::trafficIndexToTrafficIndexDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/query-result")
    public List<Map<String, Object>> queryResult(LteKpiQueryVM vm) throws ParseException {
        log.debug("视图模型：" + vm);
        List<Map<String, Object>> list = lteKpiQueryService.queryResult(vm);
        return list;
    }

    @PostMapping("/download-data")
    @ResponseBody
    public void downloadData(LteKpiQueryVM vm, HttpServletResponse response) throws ParseException {
        log.debug("视图模型：" + vm);
        lteKpiQueryService.downloadData(vm, response);
    }
}
