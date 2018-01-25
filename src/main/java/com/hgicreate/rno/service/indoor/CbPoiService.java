package com.hgicreate.rno.service.indoor;

import com.hgicreate.rno.domain.indoor.CbBuilding;
import com.hgicreate.rno.domain.indoor.CbPoi;
import com.hgicreate.rno.repository.indoor.CbBuildingRepository;
import com.hgicreate.rno.repository.indoor.CbPoiRepository;
import com.hgicreate.rno.service.indoor.dto.CbPoiDTO;
import com.hgicreate.rno.service.indoor.mapper.CbPoiMapper;
import com.hgicreate.rno.web.rest.indoor.vm.CbPoiDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CbPoiService {

    private final CbPoiRepository cbPoiRepository;
    private final CbBuildingRepository cbBuildingRepository;
    public CbPoiService(CbPoiRepository cbPoiRepository, CbBuildingRepository cbBuildingRepository) {
        this.cbPoiRepository = cbPoiRepository;
        this.cbBuildingRepository = cbBuildingRepository;
    }

    public List<CbPoiDTO> cbPoiQuery(CbPoiDataQueryVM vm){
        return cbPoiRepository.findTop1000ByBuildingIdAndPoiNameContainingAndFloorIdContainingAndPoiTypeContainingAndStatusContaining(
                vm.getBuildingId(),vm.getPoiName(),vm.getFloorId(),vm.getPoiType(),vm.getStatus()).
                stream().
                map(CbPoiMapper.INSTANCE::cbPoiToCbPoiDTO).
                collect(Collectors.toList());
    }

    public int updateCbPoiStatus(CbPoiDataQueryVM vm){
        String []str = vm.getPoiIds().split(",");
        int n=0;
        for (int i=0 ;i<str.length;i++){
            cbPoiRepository.updatePoiStatus(vm.getStatus(),Long.parseLong(str[i]));
            n++;
        }
        return n;
    }

    public void saveCbPoi(CbPoi cbPoi){
        CbBuilding cbBuilding = cbBuildingRepository.findByBuildingId(cbPoi.getBuildingId());
        cbPoi.setProv(Long.toString(cbBuilding.getArea1().getId()));
        cbPoi.setCity(Long.toString(cbBuilding.getArea().getId()));
        cbPoi.setDistrict(cbBuilding.getDistrict());
        cbPoiRepository.save(cbPoi);
    }

    public CbPoi editCbPoi(CbPoiDataQueryVM vm){
        return cbPoiRepository.findByPoiId(vm.getPoiId());
    }

    public void deleteCbPoiByPoiId(CbPoiDataQueryVM vm){
        cbPoiRepository.deleteByPoiId(vm.getPoiId());
    }
}
