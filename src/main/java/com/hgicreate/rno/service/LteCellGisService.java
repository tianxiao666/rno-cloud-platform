package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.LteNcell;
import com.hgicreate.rno.repository.LteCellGisRepository;
import com.hgicreate.rno.repository.NcellRepository;
import com.hgicreate.rno.service.dto.LteCellGisDTO;
import com.hgicreate.rno.service.mapper.LteCellGisMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class LteCellGisService {
    private final LteCellGisRepository lteCellGisRepository;
    private final NcellRepository ncellRepository;

    public LteCellGisService(LteCellGisRepository lteCellGisRepository, NcellRepository ncellRepository) {
        this.lteCellGisRepository = lteCellGisRepository;
        this.ncellRepository = ncellRepository;
    }

    @Transactional(readOnly = true)
    public List<LteCellGisDTO> getCellByCellId(String cellId) {
        return lteCellGisRepository.findOneByCellId(cellId).stream().
                map(LteCellGisMapper.INSTANCE::toLteCellGisDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getNcellByCellId(String cellId) {
       return ncellRepository.findAllByCellId(cellId).stream().map(LteNcell::getNcellId).
                collect(Collectors.toList());
    }

}
