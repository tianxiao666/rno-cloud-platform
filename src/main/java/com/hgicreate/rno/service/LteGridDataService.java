package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.service.dto.LteGridDataImportFileDTO;
import com.hgicreate.rno.service.dto.LteGridDataResultDTO;
import com.hgicreate.rno.service.mapper.LteGridDataImportMapper;
import com.hgicreate.rno.web.rest.vm.LteGridDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteGridDataQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteGridDataService {

    private final DataJobRepository dataJobRepository;

    public LteGridDataService(DataJobRepository dataJobRepository) {
        this.dataJobRepository = dataJobRepository;
    }

    public List<LteGridDataImportFileDTO> importQuery(LteGridDataImportVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBegUploadDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndUploadDate()+" 23:59:59");
        List<DataJob> list;
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    beginDate,endDate,"LTE-GRID-DATA");
        }else{
            list = dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    vm.getStatus(),beginDate,endDate,"LTE-GRID-DATA");
        }
        return list.stream()
                   .map(LteGridDataImportMapper.INSTANCE::gridDataImportFileToGridDataImportFileDTO)
                   .collect(Collectors.toList());
    }

    public List<LteGridDataResultDTO> dataQuery(LteGridDataQueryVM vm){
        List<LteGridDataResultDTO> dtoList = new ArrayList<>();
        dtoList.add(new LteGridDataResultDTO("广州市","2017-11-05 11:35:49",
                "24","020-0040-gz","113.28300870813194,23.10955693866624","2017/11/16 16:34:56"));
        return dtoList;
    }
}
