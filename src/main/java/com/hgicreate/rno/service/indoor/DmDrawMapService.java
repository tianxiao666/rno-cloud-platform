package com.hgicreate.rno.service.indoor;

import com.hgicreate.rno.repository.indoor.DmDrawMapRepository;
import com.hgicreate.rno.service.indoor.dto.DmDrawMapDTO;
import com.hgicreate.rno.service.indoor.mapper.DmDrawMapMapper;
import com.hgicreate.rno.web.rest.indoor.vm.DmDrawMapDataQueryVM;
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
public class DmDrawMapService {

    private final DmDrawMapRepository dmDrawMapRepository;

    public DmDrawMapService(DmDrawMapRepository dmDrawMapRepository) {
        this.dmDrawMapRepository = dmDrawMapRepository;
    }

    public List<DmDrawMapDTO> dmDrawMapQuery(DmDrawMapDataQueryVM vm){
        return dmDrawMapRepository.findTop1000ByBuildingIdAndDmTopicContainingAndFloorIdContainingAndStatusContaining(
                vm.getBuildingId(),vm.getDmTopic(),vm.getFloorId(),vm.getStatus()).
                stream().
                map(DmDrawMapMapper.INSTANCE::dmDrawMapToDmDrawMapDTO).
                collect(Collectors.toList());
    }

    public int updateFloorPlanegraphStatus(DmDrawMapDataQueryVM vm){
        String []str = vm.getDrawMapIds().split(",");
        int n=0;
        for (int i=0 ;i<str.length;i++){
            dmDrawMapRepository.updateDmDrawMapStatus(vm.getStatus(),Long.parseLong(str[i]));
            n++;
        }
        return n;
    }

    public List<DmDrawMapDTO> dmDrawMapQueryByFloorId(DmDrawMapDataQueryVM vm){
        return dmDrawMapRepository.findByFloorId(vm.getFloorId()).
                stream().
                map(DmDrawMapMapper.INSTANCE::dmDrawMapToDmDrawMapDTO).
                collect(Collectors.toList());
    }


}
