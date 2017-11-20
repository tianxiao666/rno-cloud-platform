package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteHoDescRepository;
import com.hgicreate.rno.service.dto.LteHoDataImportDTO;
import com.hgicreate.rno.service.dto.LteHoDescDTO;
import com.hgicreate.rno.service.mapper.LteHoDataImportMapper;
import com.hgicreate.rno.service.mapper.LteHoDescMapper;
import com.hgicreate.rno.web.rest.vm.LteHoDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteHoDataQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteHoDataService {
    private final DataJobRepository dataJobRepository;

    private final LteHoDescRepository lteHoDescRepository;

    public LteHoDataService(DataJobRepository dataJobRepository, LteHoDescRepository lteHoDescRepository) {
        this.dataJobRepository = dataJobRepository;
        this.lteHoDescRepository = lteHoDescRepository;
    }

    public List<LteHoDataImportDTO> queryImport(LteHoDataImportVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBegUploadDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndUploadDate()+" 23:59:59");
        List<DataJob> list;
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    beginDate,endDate,"LTE-HO-DATA");
        }else{
            list = dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    vm.getStatus(),beginDate,endDate,"LTE-HO-DATA");
        }
        return list.stream()
                .map(LteHoDataImportMapper.INSTANCE::hoDataImportToHoDataImportDTO)
                .collect(Collectors.toList());
    }

    public List<LteHoDescDTO> dataQuery(LteHoDataQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBeginRecordDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndRecordDate()+" 23:59:59");
        List<LteHoDescDTO> dtoList;
        if(vm.getVendor().equals("全部")){
            dtoList = lteHoDescRepository.findTop1000ByAreaAndRecordDateBetweenOrderByCreatedDateDesc(area,beginDate,endDate)
                    .stream()
                    .map(LteHoDescMapper.INSTANCE::hoDescToHoDescDTO)
                    .collect(Collectors.toList());
        }else{
            dtoList = lteHoDescRepository.findTop1000ByAreaAndVendorAndRecordDateBetweenOrderByCreatedDateDesc(area,vm.getVendor(),beginDate,endDate)
                    .stream()
                    .map(LteHoDescMapper.INSTANCE::hoDescToHoDescDTO)
                    .collect(Collectors.toList());
        }

        return dtoList;
    }
}
