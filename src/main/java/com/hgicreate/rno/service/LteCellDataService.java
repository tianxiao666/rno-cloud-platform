package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteCellDataRepository;
import com.hgicreate.rno.repository.OriginFileRepository;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.service.dto.LteCellDataFileDTO;
import com.hgicreate.rno.service.dto.LteCellDataRecordDTO;
import com.hgicreate.rno.service.mapper.LteCellDataFileMapper;
import com.hgicreate.rno.service.mapper.LteCellDataMapper;
import com.hgicreate.rno.web.rest.vm.LteCellDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteCellDataVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class LteCellDataService {


    private final LteCellDataRepository lteCellDataRepository;

    private final OriginFileRepository originFileRepository;

    private final DataJobRepository dataJobRepository;

    public LteCellDataService(LteCellDataRepository lteCellDataRepository, OriginFileRepository originFileRepository, DataJobRepository dataJobRepository) {
        this.lteCellDataRepository = lteCellDataRepository;
        this.originFileRepository = originFileRepository;
        this.dataJobRepository = dataJobRepository;
    }


    public List<LteCellDataDTO> queryLteCell(LteCellDataVM lteCellDataVM){
        Cell cell = new Cell();
        Area area = new Area();
        area.setId(Long.parseLong(lteCellDataVM.getCityId()));
        cell.setArea(area);

        if(!lteCellDataVM.getCellId().trim().equals("")){
            cell.setCellId(lteCellDataVM.getCellId().trim());
        }
        cell.setCellName(lteCellDataVM.getCellName().trim());
        ExampleMatcher matcher =  ExampleMatcher.matching()
                    .withMatcher("cellName", ExampleMatcher.GenericPropertyMatcher::contains)
                    .withIgnoreNullValues();
        if(!lteCellDataVM.getPci().trim().equals("")){
            cell.setPci(lteCellDataVM.getPci().trim());
        }
        Example<Cell> example = matcher==null?Example.of(cell): Example.of(cell, matcher);
        List<Cell> cells =lteCellDataRepository.findAll(example, new PageRequest(0,1000)).getContent();
        return cells.stream().map(LteCellDataMapper.INSTANCE::lteCellDataToLteCellDto).collect(Collectors.toList());
    }

    public List<LteCellDataFileDTO> queryFileUploadRecord(LteCellDataImportVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        Date beginDate=sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date endDate =sdf2.parse(vm.getEndUploadDate() +" 23:59:59");
        List<DataJob> list = new ArrayList<>();
        if(vm.getStatus().equals("全部")){
            list= dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate,"LTE-CELL-DATA");
        }else{

            list= dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate,"LTE-CELL-DATA");
        }
        return list.stream().map(LteCellDataFileMapper.INSTANCE::lteCellDataFileToLteCellDataFileDto)
                .collect(Collectors.toList());
    }

    public List<LteCellDataRecordDTO> queryRecord(){
        List<LteCellDataRecordDTO> list = new ArrayList<>();
        list.add(new LteCellDataRecordDTO("广州","2015-06-18 09:33:05","2015-06-18 09:36:13","4218","63243","2015-06-18 09:32:55"));
        list.add(new LteCellDataRecordDTO("广州","2015-06-20 13:45:11","2015-06-20 13:49:01","4217","52134","2015-06-20 13:44:11"));
        list.add(new LteCellDataRecordDTO("广州","2015-01-03 15:21:01","2015-01-03 15:22:12","4219","5543","2015-01-03 15:20:08"));
        return list;
    }
}
