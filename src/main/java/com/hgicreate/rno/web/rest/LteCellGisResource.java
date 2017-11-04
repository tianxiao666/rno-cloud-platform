package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.service.AreaService;
import com.hgicreate.rno.service.dto.AreaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lte-cell-gis")
public class LteCellGisResource {

    private final AreaService areaService;

    public LteCellGisResource(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping("/areas")
    public List<AreaDTO> getAllAreas(long parentId) {
        return areaService.getAreasByParentId(parentId);
    }
}
