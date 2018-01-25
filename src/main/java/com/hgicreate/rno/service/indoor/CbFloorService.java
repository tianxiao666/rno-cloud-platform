package com.hgicreate.rno.service.indoor;

import com.hgicreate.rno.domain.indoor.CbFloor;
import com.hgicreate.rno.repository.indoor.CbFloorRepository;
import com.hgicreate.rno.service.indoor.dto.CbFloorDTO;
import com.hgicreate.rno.service.indoor.mapper.CbFloorMapper;
import com.hgicreate.rno.web.rest.indoor.vm.CbFloorQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CbFloorService {

    private final CbFloorRepository cbFloorRepository;

    public CbFloorService(CbFloorRepository cbFloorRepository) {
        this.cbFloorRepository = cbFloorRepository;
    }

    public List<CbFloorDTO> cbFloorQuery(CbFloorQueryVM vm){
        return cbFloorRepository.findTop1000ByBuildingIdAndFloorNameContainingAndStatusContaining(vm.getBuildingId(),vm.getFloorName(),vm.getStatus()).
                stream().
                map(CbFloorMapper.INSTANCE::cbFloorToCbFloorDTO).
                collect(Collectors.toList());
    }

    public int updateFloorStatus(CbFloorQueryVM vm){
        String []str = vm.getBuildingIds().split(",");
        int n=0;
        for (int i=0 ;i<str.length;i++){
            cbFloorRepository.updateFloorStatus(vm.getStatus(),Long.parseLong(str[i]));
            n++;
        }
        return n;
    }

    public void deleteCbFloorByFloorId(CbFloorQueryVM vm){
        cbFloorRepository.deleteByFloorId(vm.getFloorId());
    }

    public CbFloor editCbFloor(CbFloorQueryVM vm){
        return cbFloorRepository.findByFloorId(vm.getFloorId());
    }

    public void saveCbFloor(CbFloor cbFloor){
        cbFloorRepository.save(cbFloor);
    }

    public int cbFloorUpdate(CbFloor cbFloor){
        return cbFloorRepository.updateFloor(cbFloor.getFloorName(),cbFloor.getFloorType(),cbFloor.getPhysicalFloor(),cbFloor.getBasement(),cbFloor.getFloorNote(),cbFloor.getFloorId());
    }

    public List<CbFloor> findByBuildingId(CbFloorQueryVM vm){
        return cbFloorRepository.findByBuildingId(vm.getBuildingId());
    }
}
