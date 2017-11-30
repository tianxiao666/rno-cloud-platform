package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteGridDescRepository;
import com.hgicreate.rno.service.dto.LteGridDataImportFileDTO;
import com.hgicreate.rno.service.dto.LteGridDescDTO;
import com.hgicreate.rno.service.mapper.LteGridDataImportMapper;
import com.hgicreate.rno.service.mapper.LteGridDescMapper;
import com.hgicreate.rno.web.rest.vm.LteGridDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteGridDataQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteGridDataService {

    private final DataJobRepository dataJobRepository;

    private final LteGridDescRepository lteGridDescRepository;

    public LteGridDataService(DataJobRepository dataJobRepository, LteGridDescRepository lteGridDescRepository) {
        this.dataJobRepository = dataJobRepository;
        this.lteGridDescRepository = lteGridDescRepository;
    }

    public List<LteGridDataImportFileDTO> importQuery(LteGridDataImportVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBegUploadDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndUploadDate()+" 23:59:59");
        List<DataJob> list;
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository
                    .findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                            area, beginDate,endDate,"LTE-GRID-DATA");
        }else{
            list = dataJobRepository
                    .findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                            area, vm.getStatus(),beginDate,endDate,"LTE-GRID-DATA");
        }
        return list.stream()
                   .map(LteGridDataImportMapper.INSTANCE::gridDataImportFileToGridDataImportFileDTO)
                   .collect(Collectors.toList());
    }

    public List<LteGridDescDTO> dataQuery(LteGridDataQueryVM vm){
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        List<LteGridDescDTO> dtoList;
        if(vm.getGridType().equals("全部")){
            dtoList = lteGridDescRepository.findTop1000ByAreaOrderByCreatedDateDesc(area)
                                           .stream()
                                           .map(LteGridDescMapper.INSTANCE::gridDescToGridDescDTO)
                                           .collect(Collectors.toList());
        }else{
            dtoList = lteGridDescRepository.findTop1000ByAreaAndGridTypeOrderByCreatedDateDesc(area,vm.getGridType())
                                           .stream()
                                           .map(LteGridDescMapper.INSTANCE::gridDescToGridDescDTO)
                                           .collect(Collectors.toList());
        }
        return dtoList;
    }
}
