package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.service.AreaService;
import com.hgicreate.rno.service.dto.AreaDTO;
import com.hgicreate.rno.service.mapper.AreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class AreaResource {

    private final AreaService areaService;

    public AreaResource(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping("/areas")
    public List<AreaDTO> getAllAreas(Long parentId) {
        return areaService.getAreasByParentId(parentId);
    }
}
