package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.service.dto.LteTrafficDataDTO;
import com.hgicreate.rno.service.mapper.LteTrafficDataFileMapper;
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

    public LteTrafficDataService(DataJobRepository dataJobRepository) {
        this.dataJobRepository = dataJobRepository;
    }

    public List<LteTrafficDataDTO> queryTrafficDataCollectDTOs(LteTrafficImportQueryVM vm) throws ParseException {
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
}
