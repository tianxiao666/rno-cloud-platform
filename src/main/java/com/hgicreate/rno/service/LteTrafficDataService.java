package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.TrafficDesc;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.TrafficDataRepository;
import com.hgicreate.rno.service.dto.LteTrafficDataDTO;
import com.hgicreate.rno.service.dto.LteTrafficDescDTO;
import com.hgicreate.rno.service.mapper.LteTrafficDataFileMapper;
import com.hgicreate.rno.service.mapper.LteTrafficDescMapper;
import com.hgicreate.rno.web.rest.vm.LteTrafficDataDescVM;
import com.hgicreate.rno.web.rest.vm.LteTrafficImportQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteTrafficDataService {

    private final DataJobRepository dataJobRepository;
    private final TrafficDataRepository trafficDataRepository;

    public LteTrafficDataService(DataJobRepository dataJobRepository,TrafficDataRepository trafficDataRepository) {
        this.dataJobRepository = dataJobRepository;
        this.trafficDataRepository = trafficDataRepository;
    }

    public List<LteTrafficDataDTO> queryTrafficData(LteTrafficImportQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        Date beginDate=sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date endDate =sdf2.parse(vm.getEndUploadDate() +" 23:59:59");
        List<DataJob> list = new ArrayList<>();
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate,"LTE-TRAFFIC-DATA");
        }else{
            list= dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate,"LTE-TRAFFIC-DATA");
        }
        return list.stream().map(LteTrafficDataFileMapper.INSTANCE::lteTrafficDataFileToLteTrafficDataDTO)
                .collect(Collectors.toList());
    }

    public List<LteTrafficDescDTO> queryRecord(LteTrafficDataDescVM vm) throws ParseException{
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate = sdf.parse(vm.getBeginTestDate());
        Date endDate =sdf2.parse(vm.getEndTestDate() + " 23:59:59");
        List<TrafficDesc> list = trafficDataRepository.findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
                Long.parseLong(vm.getCity()),
                beginDate,
                endDate
        );
        return list.stream().map(LteTrafficDescMapper.INSTANCE::lteTrafficDescToLteTrafficDescDTO)
                .collect(Collectors.toList());
    }

}
