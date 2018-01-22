package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.repository.gsm.GsmHwNcsDescRepository;
import com.hgicreate.rno.service.gsm.dto.GsmNcsDescQueryDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmNcsDescQueryMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsDescQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GsmNcsDescService {
    private final GsmHwNcsDescRepository gsmHwNcsDescRepository;
    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;

    public GsmNcsDescService(GsmHwNcsDescRepository gsmHwNcsDescRepository, GsmEriNcsDescRepository gsmEriNcsDescRepository) {
        this.gsmHwNcsDescRepository = gsmHwNcsDescRepository;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
    }

    public List<GsmNcsDescQueryDTO> ncsDescQuery(GsmNcsDescQueryVM vm) {
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        log.debug("开始时间：{}",vm.getBeginTestDate());
        log.debug("修改前的结束时间：{}",vm.getEndTestDate());
        calendar.setTime(vm.getEndTestDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        log.debug("修改后时间：{}",endDate);
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")) {
            if (Objects.equals(vm.getFactory(), "HW")) {
                return gsmHwNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(
                        area, vm.getBeginTestDate(), endDate)
                        .stream().map(GsmNcsDescQueryMapper.INSTANCE::hwNcsDescQueryToDTO)
                        .collect(Collectors.toList());
            }
            if (Objects.equals(vm.getFactory(), "ERI")) {
                return gsmEriNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(
                        area, vm.getBeginTestDate(), endDate)
                        .stream().map(GsmNcsDescQueryMapper.INSTANCE::eriNcsDescQueryToDTO)
                        .collect(Collectors.toList());
            }
        }
        if (Objects.equals(vm.getFactory(), "HW")) {
            return gsmHwNcsDescRepository.findTop1000ByAreaAndBscLikeAndMeaTimeBetween(
                    area,"%" + vm.getBsc().trim() + "%", vm.getBeginTestDate(), endDate)
                    .stream().map(GsmNcsDescQueryMapper.INSTANCE::hwNcsDescQueryToDTO)
                    .collect(Collectors.toList());
        }
        if (Objects.equals(vm.getFactory(), "ERI")) {
            return gsmEriNcsDescRepository.findTop1000ByAreaAndBscLikeAndMeaTimeBetween(
                    area, "%" + vm.getBsc().trim() + "%", vm.getBeginTestDate(), endDate)
                    .stream().map(GsmNcsDescQueryMapper.INSTANCE::eriNcsDescQueryToDTO)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
