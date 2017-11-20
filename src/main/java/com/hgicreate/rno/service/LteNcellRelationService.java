package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.mapper.LteNcellRelationQueryMapper;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteNcellDescRepository;
import com.hgicreate.rno.service.dto.LteNcellDescDTO;
import com.hgicreate.rno.service.dto.LteNcellImportFileDTO;
import com.hgicreate.rno.service.dto.LteNcellRelationDTO;
import com.hgicreate.rno.service.mapper.LteNcellDescMapper;
import com.hgicreate.rno.service.mapper.LteNcellImportFileMapper;
import com.hgicreate.rno.service.mapper.LteNcellRelationMapper;
import com.hgicreate.rno.web.rest.vm.LteNcellImportDtQueryVM;
import com.hgicreate.rno.web.rest.vm.LteNcellImportQueryVM;
import com.hgicreate.rno.web.rest.vm.LteNcellRelationQueryVM;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LteNcellRelationService {

    private final LteNcellRelationQueryMapper lteNcellRelationQueryMapper;
    private final DataJobRepository dataJobRepository;
    private final LteNcellDescRepository lteNcellDescRepository;

    public LteNcellRelationService(LteNcellRelationQueryMapper lteNcellRelationQueryMapper, DataJobRepository dataJobRepository, LteNcellDescRepository lteNcellDescRepository) {
        this.lteNcellRelationQueryMapper = lteNcellRelationQueryMapper;
        this.dataJobRepository = dataJobRepository;
        this.lteNcellDescRepository = lteNcellDescRepository;
    }

    public List<LteNcellRelationDTO> queryNcellRelationDTOs(LteNcellRelationQueryVM vm) {
        List<LteNcellRelationDTO> dtoList = lteNcellRelationQueryMapper.queryNcellRelation(vm)
                                               .stream()
                                               .map(LteNcellRelationMapper.INSTANCE::ncellRelationToNcellRelationDTO)
                                               .collect(Collectors.toList());
        return dtoList;
    }

    public  List<LteNcellImportFileDTO> queryImport(LteNcellImportQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate=sdf.parse(vm.getBegUploadDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndUploadDate()+" 23:59:59");
        List<DataJob> list;
        if(vm.getStatus().equals("全部")){
            list = dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    beginDate,endDate,"LTE-NCELL-RELATION-DATA");
        }else{
            list = dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area,
                    vm.getStatus(),beginDate,endDate,"LTE-NCELL-RELATION-DATA");
        }
        return list.stream()
                   .map(LteNcellImportFileMapper.INSTANCE::ncellImportFileToNcellImportFileDTO)
                   .collect(Collectors.toList());
    }

    public List<LteNcellDescDTO> queryImportDt(LteNcellImportDtQueryVM vm){
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityId()));
        List<LteNcellDescDTO> dtoList;
        if(vm.getDataType().equals("全部")){
            dtoList = lteNcellDescRepository.findTop1000ByAreaOrderByCreatedDateDesc(area)
                                            .stream()
                                            .map(LteNcellDescMapper.INSTANCE::ncellDescToNcellDescDTO)
                                            .collect(Collectors.toList());
        }else{
            dtoList = lteNcellDescRepository.findTop1000ByAreaAndAndDataTypeOrderByCreatedDateDesc(area,vm.getDataType())
                                            .stream()
                                            .map(LteNcellDescMapper.INSTANCE::ncellDescToNcellDescDTO)
                                            .collect(Collectors.toList());
        }
        return dtoList;
    }
}
