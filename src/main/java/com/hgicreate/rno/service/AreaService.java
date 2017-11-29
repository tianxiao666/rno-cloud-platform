package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.service.dto.AreaDTO;
import com.hgicreate.rno.service.mapper.AreaMapper;
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
    public List<AreaDTO> getAreasByParentId(long parentId) {
        List<AreaDTO> list = areaRepository.findAllByParentId(parentId)
                .stream().map(AreaMapper.INSTANCE::areaToAreaDTO)
                .collect(Collectors.toList());

        // 如果下一级为空，则把当前区域设置为下一级，解决东莞、中山这些没有任何区县的城市在区域联动三级显示的问题
        if (list.size() == 0) {
            list.add(AreaMapper.INSTANCE.areaToAreaDTO(areaRepository.getOne(parentId)));

            // 调整港澳台在区域联动二三级的显示
            if (parentId > 0) {
                if (list.get(0).getId() == 710000) {
                    list.get(0).setName("台湾");
                } else if (list.get(0).getId() == 810000) {
                    list.get(0).setName("香港");
                } else if (list.get(0).getId() == 820000) {
                    list.get(0).setName("澳门");
                }
            }
        }

        return list;
    }

    public Area findById(Long id){
        return areaRepository.findById(id);
    }
}
