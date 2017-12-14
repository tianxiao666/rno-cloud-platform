package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.GsmStsDescriptor;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.GsmStsDescriptorRepository;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficDataDescDTO;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficDataFileDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmTrafficDataDescMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmTrafficDataFileMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficDataDescVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficDataImportVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GsmTrafficDataService {

    private final DataJobRepository dataJobRepository;

    private final GsmStsDescriptorRepository gsmStsDescriptorRepository;

    public GsmTrafficDataService(DataJobRepository dataJobRepository, GsmStsDescriptorRepository gsmStsDescriptorRepository) {
        this.dataJobRepository = dataJobRepository;
        this.gsmStsDescriptorRepository = gsmStsDescriptorRepository;
    }


    public List<GsmTrafficDataFileDTO> queryFileUploadRecord(GsmTrafficDataImportVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        Date beginDate=sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date endDate =sdf2.parse(vm.getEndUploadDate() +" 23:59:59");
        List<DataJob> list;
        if(vm.getStatus().equals("全部")){
            list= dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate,"GSM-TRAFFIC-DATA");
        }else{

            list= dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate,"GSM-TRAFFIC-DATA");
        }
        return list.stream().map(GsmTrafficDataFileMapper.INSTANCE::gsmTrafficFileToGsmTrafficFileDto)
                .collect(Collectors.toList());
    }

    public List<GsmTrafficDataDescDTO> queryTrafficDataDesc(GsmTrafficDataDescVM vm) throws ParseException{
        Area area = new Area();
        area.setId(Long.parseLong(vm.getAreaId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        Date beginDate=sdf.parse(vm.getBeginTime());
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date endDate =sdf2.parse(vm.getLatestAllowedTime() +" 23:59:59");
        List<GsmStsDescriptor> list;
        if("-1".equals(vm.getStsPeriod())){
            list= gsmStsDescriptorRepository.findTop1000ByArea_IdAndStsDateBetweenAndSpecTypeOrderByCreateTimeDesc(
                    Long.parseLong(vm.getAreaId()), beginDate,endDate, vm.getSearchType()
            );
        }else{
            list= gsmStsDescriptorRepository.findTop1000ByArea_IdAndStsDateBetweenAndSpecTypeAndStsPeriodOrderByCreateTimeDesc(
                    Long.parseLong(vm.getAreaId()), beginDate,endDate, vm.getSearchType(),vm.getStsPeriod()
            );
        }
        return list.stream().map(GsmTrafficDataDescMapper.INSTANCE::trafficDataToTrafficDataDTO)
                .collect(Collectors.toList());
    }
}
