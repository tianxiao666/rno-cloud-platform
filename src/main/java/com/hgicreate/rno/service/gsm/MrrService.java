package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.MrrDesc;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.MrrDescRepository;
import com.hgicreate.rno.web.rest.gsm.vm.MrrDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.MrrImportQueryVM;
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
public class MrrService {
    private final MrrDescRepository mrrDescRepository;
    private final DataJobRepository dataJobRepository;

    public MrrService(MrrDescRepository mrrDescRepository, DataJobRepository dataJobRepository) {
        this.mrrDescRepository = mrrDescRepository;
        this.dataJobRepository = dataJobRepository;
    }

    public List<MrrDesc> mrrDataQuery(MrrDescQueryVM vm){
        Area area = new Area();
        area.setId(vm.getAreaId());
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")){
            return mrrDescRepository.findByAreaAndFactoryAndMeaDateBetween(area, vm.getFactory(), vm.getBeginTestDate(), vm.getEndTestDate());
        }
        return mrrDescRepository.findByAreaAndFactoryAndBscAndMeaDateBetween(area, vm.getFactory(), vm.getBsc(), vm.getBeginTestDate(), vm.getEndTestDate());

    }

    public List<DataJob> mrrImportQuery(MrrImportQueryVM vm){
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getEndDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if ("全部".equals(vm.getStatus())){
            return dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area, vm.getBeginDate(), endDate, Constants.MRR_DATA_TYPE);
        }
        return dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area, vm.getStatus(), vm.getBeginDate(), endDate,Constants.MRR_DATA_TYPE);
    }
}
