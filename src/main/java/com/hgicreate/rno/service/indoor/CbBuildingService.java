package com.hgicreate.rno.service.indoor;

import com.hgicreate.rno.config.indoor.SvgUtils;
import com.hgicreate.rno.domain.indoor.CbBuilding;
import com.hgicreate.rno.repository.indoor.CbBuildingRepository;
import com.hgicreate.rno.service.indoor.dto.CbBuildingDTO;
import com.hgicreate.rno.service.indoor.mapper.CbBuildingMapper;
import com.hgicreate.rno.web.rest.indoor.vm.CbBuildingDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CbBuildingService {


    private final CbBuildingRepository cbBuildingRepository;

    public CbBuildingService(CbBuildingRepository cbBuildingRepository) {
        this.cbBuildingRepository = cbBuildingRepository;
    }

    public List<CbBuildingDTO> queryCbBuildingDTOs(CbBuildingDataQueryVM vm) {
        return cbBuildingRepository.findTop1000ByBuildingNameContainingAndBuildTypeContaining(vm.getBuildingName(), vm.getBuildingType()).
                stream().
                map(CbBuildingMapper.INSTANCE::cbBuildingTocbBuildingDTO).
                collect(Collectors.toList());
    }

    public CbBuilding editCbBuilding(CbBuildingDataQueryVM vm){
        return cbBuildingRepository.findByBuildingId(vm.getBuildingId());
    }

    public void saveCbBuilding(CbBuilding cbBuilding){
        cbBuilding.setLtLongitudel(SvgUtils.getEncodeLatLng(cbBuilding.getLtLongitudel()));
        cbBuilding.setLtLatitudel(SvgUtils.getEncodeLatLng(cbBuilding.getLtLatitudel()));
        cbBuilding.setRbLongitudel(SvgUtils.getEncodeLatLng(cbBuilding.getRbLongitudel()));
        cbBuilding.setRbLatitudel(SvgUtils.getEncodeLatLng(cbBuilding.getRbLatitudel()));
        cbBuildingRepository.save(cbBuilding);
    }

    public int updateCbBuildingStatus(CbBuildingDataQueryVM vm){
        String []str = vm.getBuildingIds().split(",");
        int n=0;
        for (int i=0 ;i<str.length;i++){
            cbBuildingRepository.updateBuildingStatus(vm.getStatus(),Long.parseLong(str[i]));
            n++;
        }
        return n;
    }

    public int cbBuildingUpdate(CbBuilding cbBuilding){
      double LtLongitudel =  SvgUtils.getEncodeLatLng(cbBuilding.getLtLongitudel());
      double LtLatitudel =  SvgUtils.getEncodeLatLng(cbBuilding.getLtLatitudel());
      double RbLongitudel =  SvgUtils.getEncodeLatLng(cbBuilding.getRbLongitudel());
      double RbLatitudel =  SvgUtils.getEncodeLatLng(cbBuilding.getRbLatitudel());
      return cbBuildingRepository.updateBuilding(cbBuilding.getBuildingName(),cbBuilding.getArea1().getId(),cbBuilding.getArea().getId(),
              cbBuilding.getPostalcode(),cbBuilding.getAddress(),cbBuilding.getBuildType(),cbBuilding.getTotalFloor(),cbBuilding.getPhone(),
              cbBuilding.getSite(),LtLongitudel,LtLatitudel,RbLongitudel,RbLatitudel,cbBuilding.getNote(),cbBuilding.getStatus(),cbBuilding.getDistrict(),cbBuilding.getBuildingId());
    }

    public void deleteCbBuildingByBuildingId(CbBuildingDataQueryVM vm){
        cbBuildingRepository.deleteByBuildingId(vm.getBuildingId());
    }

    public List<CbBuilding> findAll(){
        return cbBuildingRepository.findAll(new Sort(Sort.DEFAULT_DIRECTION.ASC,"buildingId"));
    }

}
