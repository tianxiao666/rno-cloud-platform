package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.DtDesc;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.DtDescRepository;
import com.hgicreate.rno.service.dto.LteDtDataFileDTO;
import com.hgicreate.rno.service.dto.LteDtDescDTO;
import com.hgicreate.rno.service.mapper.LteDtDataFileMapper;
import com.hgicreate.rno.service.mapper.LteDtDescMapper;
import com.hgicreate.rno.web.rest.vm.LteDtDescVM;
import com.hgicreate.rno.web.rest.vm.LteDtImportQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteDtDataService {

    private final DataJobRepository dataJobRepository;

    private final DtDescRepository dtDescRepository;

    public LteDtDataService(DataJobRepository dataJobRepository, DtDescRepository dtDescRepository) {
        this.dataJobRepository = dataJobRepository;
        this.dtDescRepository = dtDescRepository;
    }

    public List<LteDtDataFileDTO> queryDataCollectDTOs(LteDtImportQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        Date beginDate=sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date endDate =sdf2.parse(vm.getEndUploadDate() +" 23:59:59");
        List<DataJob> list = new ArrayList<>();
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate,"LTE-DT-DATA");
        }else{
            list= dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate,"LTE-DT-DATA");
        }
        return list.stream().map(LteDtDataFileMapper.INSTANCE::lteDtDataFileToLteDtDataFileDto)
                .collect(Collectors.toList());
    }

    public List<LteDtDescDTO> queryRecord(LteDtDescVM vm) throws ParseException{
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate = sdf.parse(vm.getBeginTestDate());
        Date endDate =sdf2.parse(vm.getEndTestDate() + " 23:59:59");
        List<DtDesc> list = new ArrayList<>();
        if(vm.getDataType().equals("全部")){
            list = dtDescRepository.findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
                    Long.parseLong(vm.getCity2()),
                    beginDate,
                    endDate);
        }else{
            list = dtDescRepository.findTop1000ByArea_IdAndDataTypeAndCreatedDateBetweenOrderByCreatedDateDesc(
                    Long.parseLong(vm.getCity2()),
                    vm.getDataType(),
                    beginDate,
                    endDate);
        }
        return list.stream().map(LteDtDescMapper.INSTANCE::lteDtDescToLteDtDescDTO)
                .collect(Collectors.toList());
    }
}
