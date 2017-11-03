package com.hgicreate.rno.service;

import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.service.dto.AreaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AreaService {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public List<AreaDTO> getAllAreas() {
        return areaRepository.findAll().stream().map(AreaDTO::new).collect(Collectors.toList());
    }

    public List<AreaDTO> getAreasByParentId(long parentId) {
        return areaRepository.findAllByParentId(parentId)
                .stream().map(AreaDTO::new).collect(Collectors.toList());
    }
}
