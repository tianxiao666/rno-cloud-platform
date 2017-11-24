package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.GridData;
import com.hgicreate.rno.repository.GridCoordRepository;
import com.hgicreate.rno.repository.GridDataRepository;
import com.hgicreate.rno.repository.LteCellGisRepository;
import com.hgicreate.rno.service.LteGridGisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/lte-grid-gis")
public class LteGridGisResource {

    private final GridDataRepository gridDataRepository;
    private final GridCoordRepository gridCoordRepository;
    private final LteGridGisService lteGridGisService;
    private final LteCellGisRepository lteCellGisRepository;

    public LteGridGisResource(GridDataRepository gridDataRepository,
                              GridCoordRepository gridCoordRepository,
                              LteGridGisService lteGridGisService,
                              LteCellGisRepository lteCellGisRepository) {
        this.gridDataRepository = gridDataRepository;
        this.gridCoordRepository = gridCoordRepository;
        this.lteGridGisService = lteGridGisService;
        this.lteCellGisRepository = lteCellGisRepository;
    }

    @GetMapping("/grid-data")
    public Map<String, Object> getGridData(String gridType, Long areaId) {
        log.debug("gridType={}, areaId={}", gridType, areaId);
        Map<String, Object> res = new HashMap<>();
        List<GridData> gridData = gridDataRepository.findByGridTypeInAndAreaIdOrderByIdAsc
                (gridType.split(","), areaId);
        res.put("gridData", gridData);
        res.put("gridCoords", gridCoordRepository.findByGridIdInOrderByGridIdAsc(
                gridData.stream().map(GridData::getId).collect(Collectors.toList())));
        return res;
    }

    @GetMapping("/download-cell-data")
    @ResponseBody
    public void download(String type, Long areaId, HttpServletResponse response) {
        log.debug("gridType={}, areaId={}", type, areaId);
        lteGridGisService.getCellDataByGrid(type, areaId, response);
    }
}
