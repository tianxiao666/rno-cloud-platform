package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.MrrDesc;
import com.hgicreate.rno.repository.gsm.MrrDescRepository;
import com.hgicreate.rno.web.rest.gsm.vm.MrrDescQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MrrDescService {
    private final MrrDescRepository mrrDescRepository;

    public MrrDescService(MrrDescRepository mrrDescRepository) {
        this.mrrDescRepository = mrrDescRepository;
    }

    public List<MrrDesc> mrrDataQuery(MrrDescQueryVM vm){
        Area area = new Area();
        area.setId(vm.getAreaId());
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")){
            return mrrDescRepository.findByAreaAndFactoryAndMeaDateBetween(area, vm.getFactory(), vm.getBeginTestDate(), vm.getEndTestDate());
        }
        return mrrDescRepository.findByAreaAndFactoryAndBscAndMeaDateBetween(area, vm.getFactory(), vm.getBsc(), vm.getBeginTestDate(), vm.getEndTestDate());
    }
}
