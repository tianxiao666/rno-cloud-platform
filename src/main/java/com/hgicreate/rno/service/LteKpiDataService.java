package com.hgicreate.rno.service;


import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.LteKpiDesc;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteKpiDescRepository;
import com.hgicreate.rno.service.dto.LteKpiDataFileDTO;
import com.hgicreate.rno.service.dto.LteKpiDescDTO;
import com.hgicreate.rno.service.mapper.LteKpiDataFileMapper;
import com.hgicreate.rno.service.mapper.LteKpiDescMapper;
import com.hgicreate.rno.web.rest.vm.LteKpiDataFileVM;
import com.hgicreate.rno.web.rest.vm.LteKpiDescVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LteKpiDataService {

    private final DataJobRepository dataJobRepository;

    private final LteKpiDescRepository lteKpiDescRepository;

    public LteKpiDataService(DataJobRepository dataJobRepository, LteKpiDescRepository lteKpiDescRepository) {
        this.dataJobRepository = dataJobRepository;
        this.lteKpiDescRepository = lteKpiDescRepository;
    }

    public List<LteKpiDataFileDTO> queryFileUploadRecord(LteKpiDataFileVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        Date beginDate=sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date endDate =sdf2.parse(vm.getEndUploadDate() +" 23:59:59");
        List<DataJob> list = new ArrayList<>();
        if(vm.getStatus().equals("全部")){
            list= dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate,"LTE-KPI-DATA");
        }else{

            list= dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate,"LTE-KPI-DATA");
        }
        return list.stream().map(LteKpiDataFileMapper.INSTANCE::lteKpiDataFileToLteKpiDataFileDto)
                .collect(Collectors.toList());
    }

    public List<LteKpiDescDTO> queryRecord(LteKpiDescVM vm) throws ParseException{
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate = sdf.parse(vm.getKpiMeaBegDate());
        Date endDate =sdf2.parse(vm.getKpiMeaEndDate() + " 23:59:59");
        List<LteKpiDesc> list = lteKpiDescRepository.findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
                Long.parseLong(vm.getCity2()),
                beginDate,
                endDate
        );
        return list.stream().map(LteKpiDescMapper.INSTANCE::lteKpiDescToLteKpiDescDTO)
                .collect(Collectors.toList());
    }
}
