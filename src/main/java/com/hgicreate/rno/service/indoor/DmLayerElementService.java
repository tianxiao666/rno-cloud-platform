package com.hgicreate.rno.service.indoor;

import com.hgicreate.rno.repository.indoor.DmLayerElementRepository;
import com.hgicreate.rno.service.indoor.dto.DmLayerElementDTO;
import com.hgicreate.rno.service.indoor.mapper.DmLayerElementMapper;
import com.hgicreate.rno.web.rest.indoor.vm.DmLayerElementDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chao.xj
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DmLayerElementService {

    private final DmLayerElementRepository dmLayerElementRepository;

    public DmLayerElementService(DmLayerElementRepository dmLayerElementRepository) {
        this.dmLayerElementRepository = dmLayerElementRepository;
    }

    public List<DmLayerElementDTO> dmLayerElementQuery(DmLayerElementDataQueryVM vm){
        return dmLayerElementRepository.findByLayerId(vm.getLayerId()).
                stream().
                map(DmLayerElementMapper.INSTANCE::dmLayerElementToDmLayerElementDTO).
                collect(Collectors.toList());
    }
}
