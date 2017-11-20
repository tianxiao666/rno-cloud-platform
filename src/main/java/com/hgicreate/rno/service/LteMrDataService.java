package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteMrDescRepository;
import com.hgicreate.rno.service.dto.LteMrDataImportDTO;
import com.hgicreate.rno.service.dto.LteMrDescDTO;
import com.hgicreate.rno.service.mapper.LteMrDataImportMapper;
import com.hgicreate.rno.service.mapper.LteMrDescMapper;
import com.hgicreate.rno.web.rest.vm.LteMrDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteMrDataQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteMrDataService {

    private final DataJobRepository dataJobRepository;

    private final LteMrDescRepository lteMrDescRepository;

    public LteMrDataService(DataJobRepository dataJobRepository, LteMrDescRepository lteMrDescRepository) {
        this.dataJobRepository = dataJobRepository;
        this.lteMrDescRepository = lteMrDescRepository;
    }

    public  List<LteMrDataImportDTO> queryImport(LteMrDataImportVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBegUploadDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndUploadDate()+" 23:59:59");
        List<DataJob> list;
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    beginDate,endDate,"LTE-MR-DATA");
        }else{
            list = dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    vm.getStatus(),beginDate,endDate,"LTE-MR-DATA");
        }
        return list.stream()
                .map(LteMrDataImportMapper.INSTANCE::mrDataImportToMrDataImportDTO)
                .collect(Collectors.toList());
    }

    public List<LteMrDescDTO> dataQuery(LteMrDataQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBeginRecordDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndRecordDate()+" 23:59:59");
        List<LteMrDescDTO> dtoList;
        if(vm.getVendor().equals("全部")){
            dtoList = lteMrDescRepository.findTop1000ByAreaAndRecordDateBetweenOrderByCreatedDateDesc(area,beginDate,endDate)
                                         .stream()
                                         .map(LteMrDescMapper.INSTANCE::mrDescToMrDescDTO)
                                         .collect(Collectors.toList());
        }else{
            dtoList = lteMrDescRepository.findTop1000ByAreaAndVendorAndRecordDateBetweenOrderByCreatedDateDesc(area,vm.getVendor(),beginDate,endDate)
                                           .stream()
                                           .map(LteMrDescMapper.INSTANCE::mrDescToMrDescDTO)
                                           .collect(Collectors.toList());
        }

        return dtoList;
    }
}
