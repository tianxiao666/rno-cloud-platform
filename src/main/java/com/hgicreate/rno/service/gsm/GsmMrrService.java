package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.GsmMrrDescRepository;
import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrImportQueryVM;
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
public class GsmMrrService {
    private final GsmMrrDescRepository gsmMrrDescRepository;
    private final DataJobRepository dataJobRepository;

    public GsmMrrService(GsmMrrDescRepository gsmMrrDescRepository, DataJobRepository dataJobRepository) {
        this.gsmMrrDescRepository = gsmMrrDescRepository;
        this.dataJobRepository = dataJobRepository;
    }

    public List<GsmMrrDesc> mrrDataQuery(GsmMrrDescQueryVM vm){
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getEndTestDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")){
            return gsmMrrDescRepository.findTop1000ByAreaAndFactoryAndMeaDateBetween(area, vm.getFactory(), vm.getBeginTestDate(), endDate);
        }
        return gsmMrrDescRepository.findTop1000ByAreaAndFactoryAndBscAndMeaDateBetween(area, vm.getFactory(), vm.getBsc(), vm.getBeginTestDate(), endDate);

    }

    public List<DataJob> mrrImportQuery(GsmMrrImportQueryVM vm){
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
