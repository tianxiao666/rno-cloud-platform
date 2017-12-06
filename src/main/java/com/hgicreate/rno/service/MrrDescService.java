package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.MrrDesc;
import com.hgicreate.rno.repository.MrrDescRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    public List<MrrDesc> mrrDataQuery(String cityName, String factory, String bsc, Date beginTestDate, Date endTestDate){
        if (bsc == null || Objects.equals(bsc.trim(), "")){
            return mrrDescRepository.findByCityNameAndFactoryAndMeaDateBetween(cityName, factory, beginTestDate, endTestDate);
        }
        return mrrDescRepository.findByCityNameAndFactoryAndBscAndMeaDateBetween(cityName, factory, bsc, beginTestDate, endTestDate);
    }
}
