package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.DtData;
import com.hgicreate.rno.repository.DtDataForDetailRepository;
import com.hgicreate.rno.repository.DtDataRepository;
import com.hgicreate.rno.repository.DtDescRepository;
import com.hgicreate.rno.repository.DtNcellForDetailRepository;
import com.hgicreate.rno.service.LteDtAnalysisService;
import com.hgicreate.rno.service.dto.DtDataDTO;
import com.hgicreate.rno.service.dto.DtDescDTO;
import com.hgicreate.rno.service.mapper.DtDataMapper;
import com.hgicreate.rno.service.mapper.DtDescMapper;
import com.hgicreate.rno.web.rest.vm.LteDtAnalysisVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/lte-dt-analysis")
public class LteDtAnalysisResource {

    private final DtDescRepository dtDescRepository;
    private final DtDataRepository dtDataRepository;
    private final DtDataForDetailRepository dtDataForDetailRepository;
    private final DtNcellForDetailRepository dtNcellForDetailRepository;
    private final LteDtAnalysisService lteDtAnalysisService;

    public LteDtAnalysisResource(DtDescRepository dtDescRepository, DtDataRepository dtDataRepository,
                                 DtDataForDetailRepository dtDataForDetailRepository,
                                 DtNcellForDetailRepository dtNcellForDetailRepository,
                                 LteDtAnalysisService lteDtAnalysisService) {
        this.dtDescRepository = dtDescRepository;
        this.dtDataRepository = dtDataRepository;
        this.dtDataForDetailRepository = dtDataForDetailRepository;
        this.dtNcellForDetailRepository = dtNcellForDetailRepository;
        this.lteDtAnalysisService = lteDtAnalysisService;
    }

    @GetMapping("/dt-desc")
    public List<DtDescDTO> getDtDesc(@RequestParam(value = "start", defaultValue = "0") Integer start,
                                     @RequestParam(value = "length", defaultValue = "10") Integer length,
                                     LteDtAnalysisVM lteDtAnalysisVM) {
        log.debug("start={}, length={}, areaId={}, createdDate={}, dataType={}, areaType={}",
                start, length, lteDtAnalysisVM.getAreaId(), lteDtAnalysisVM.getCreatedDate(),
                lteDtAnalysisVM.getDataType(), lteDtAnalysisVM.getAreaType());

        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = new PageRequest(start, length, sort);
        return dtDescRepository.
                findByArea_IdAndDataTypeInAndAreaTypeInAndCreatedDate(lteDtAnalysisVM.getAreaId(),
                        lteDtAnalysisVM.getDataType(), lteDtAnalysisVM.getAreaType(),
                        lteDtAnalysisVM.getCreatedDate(), pageable)
                .stream().map(DtDescMapper.INSTANCE::toDtDescDto).collect(Collectors.toList());
    }

    @GetMapping("/dt-data")
    public List<DtDataDTO> getDtData(String descId) {
        log.debug("descId={}", descId);
        return dtDataRepository.findAllByDescIdInOrderByIdAsc(string2Long(descId)).stream()
                .map(DtDataMapper.INSTANCE::toDtDataDto).collect(Collectors.toList());
    }

    @GetMapping("/dt-data-detail")
    public Map<String, Object> getDtDataDetail(Long dataId) {
        log.debug("dataId={}", dataId);
        Map<String, Object> res = new HashMap<>();
        res.put("cell", dtDataForDetailRepository.findDtCellDetailById(dataId));
        res.put("ncell", dtNcellForDetailRepository.findDtNCellDetailById(dataId));
        return res;
    }

    @GetMapping("/weak-coverage")
    public List<Long> getWeakCoverage(String descId) {
        log.debug("descId={}", descId);
        List<Long> res = lteDtAnalysisService.getWeakCoverage(string2Long(descId));
        log.debug("res={}", res);
        return res;
    }

    @GetMapping("/room-leakage")
    public List<Long> getRoomLeakage(String descId) {
        log.debug("descId={}", descId);
        return dtDataRepository.findByDescIdInAndEarfcnIn(string2Long(descId), Constants.E_FREQUENCE)
                .stream().map(DtData::getId).collect(Collectors.toList());
    }

    @GetMapping("/overlap-coverage")
    public List<Long> getOverlapCoverage(String descId) {
        log.debug("descId={}", descId);
        return lteDtAnalysisService.getOverlapCoverage(string2Long(descId));
    }

    private static Long[] string2Long(String str) {
        String[] s= str.split(",");
        Long[] res = new Long[s.length];
        for (int index = 0; index < s.length; index++) {
            res[index] = Long.parseLong(s[index]);
        }
        return res;
    }

}
