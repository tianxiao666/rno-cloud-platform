package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmFasDesc;
import com.hgicreate.rno.repository.gsm.GsmFasDescRepository;
import com.hgicreate.rno.web.rest.gsm.vm.GsmFasDataQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GsmFasService {
    private final GsmFasDescRepository gsmFasDescRepository;

    public GsmFasService(GsmFasDescRepository gsmFasDescRepository) {
        this.gsmFasDescRepository = gsmFasDescRepository;
    }

    public List<GsmFasDesc> gsmFasDescQuery(GsmFasDataQueryVM vm){
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getEndTestDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")){
            return gsmFasDescRepository.findTop1000ByAreaAndMeaTimeBetween(area, vm.getBeginTestDate(), endDate);
        }
        return gsmFasDescRepository.findTop1000ByAreaAndBscLikeAndMeaTimeBetween(area,"%" + vm.getBsc().trim() + "%", vm.getBeginTestDate(), endDate);
    }
}
