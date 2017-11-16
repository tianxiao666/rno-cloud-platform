package com.hgicreate.rno.service;


import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.service.dto.LteKpiDataFileDTO;
import com.hgicreate.rno.service.dto.LteKpiDataRecordDTO;
import com.hgicreate.rno.service.mapper.LteKpiDataFileMapper;
import com.hgicreate.rno.web.rest.vm.LteKpiDataFileVM;
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

    public LteKpiDataService(DataJobRepository dataJobRepository) {
        this.dataJobRepository = dataJobRepository;
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

    public List<LteKpiDataRecordDTO> queryRecord(){
        List<LteKpiDataRecordDTO> list = new ArrayList<>();
        list.add(new LteKpiDataRecordDTO("珠海市","2016-06-18 09:33:05","","珠海市KPI数据.csv","23142","2015-06-18 09:32:55"));
        list.add(new LteKpiDataRecordDTO("珠海市","2016-06-20 13:45:11","","珠海市KPI数据.csv","821","2015-06-20 13:44:11"));
        list.add(new LteKpiDataRecordDTO("珠海市","2016-01-03 15:21:01","","珠海市01-03KPI.csv","5437","2015-01-03 15:20:08"));
        return list;
    }
}
