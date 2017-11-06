package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.service.LteCellGisService;
import com.hgicreate.rno.service.dto.LteCellGisDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lte-cell-gis")
public class LteCellGisResource {

    private final LteCellGisService lteCellGisService;

    public LteCellGisResource(LteCellGisService lteCellGisService) {
        this.lteCellGisService = lteCellGisService;
    }

    @GetMapping("/getCellByCellId")
    public List<LteCellGisDTO> getCellByCellId(String cellId) {
        return lteCellGisService.getCellByCellId(cellId);
    }

    @GetMapping("/getNcellByCellId")
    public List<String> getNcellByCellId(String cellId) {
        return lteCellGisService.getNcellByCellId(cellId);
    }
}
