package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.domain.gsm.GsmNcellRelation;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.repository.gsm.GsmNcellRelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/gsm-cell-gis")
public class GsmCellGisResource {

    private final GsmCellDataRepository gsmCellDataRepository;

    private final GsmNcellRelationRepository gsmNcellRelationRepository;

    public GsmCellGisResource(GsmCellDataRepository gsmCellDataRepository, GsmNcellRelationRepository gsmNcellRelationRepository) {
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.gsmNcellRelationRepository = gsmNcellRelationRepository;
    }

    @GetMapping("/cell-detail")
    public List<GsmCell> getCellByCellId(String cellId) {
        return gsmCellDataRepository.findByCellId(cellId);
    }

    @GetMapping("/ncell-detail")
    public List<String> getNcellByCellId(String cellId) {
        return gsmNcellRelationRepository.findByCellId(cellId).stream()
                .map(GsmNcellRelation::getNcellId).collect(Collectors.toList());
    }
}
