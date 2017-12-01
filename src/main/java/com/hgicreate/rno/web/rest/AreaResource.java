package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.service.AreaService;
import com.hgicreate.rno.service.UserService;
import com.hgicreate.rno.service.dto.AreaDTO;
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

    private final UserService userService;

    public AreaResource(AreaService areaService, UserService userService) {
        this.areaService = areaService;
        this.userService = userService;
    }

    @GetMapping("/areas")
    public List<AreaDTO> getAllAreas(Long parentId) {
        return areaService.getAreasByParentId(parentId);
    }

    @GetMapping("/get-area-by-id")
    public Area getAreaById(Long id){
        return areaService.findById(id);
    }

    @GetMapping("/get-user-default-area")
    public Long getUserDefaultArea(){
        return userService.getCurrentUser().getDefaultArea();
    }
}
