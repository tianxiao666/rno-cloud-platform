package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.LteCell;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.LteCellDesc;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.LteCellDataRepository;
import com.hgicreate.rno.repository.LteCellDescRepository;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.service.dto.LteCellDataFileDTO;
import com.hgicreate.rno.service.dto.LteCellDescDTO;
import com.hgicreate.rno.service.mapper.LteCellDataFileMapper;
import com.hgicreate.rno.service.mapper.LteCellDataMapper;
import com.hgicreate.rno.service.mapper.LteCellDescMapper;
import com.hgicreate.rno.web.rest.vm.LteCellDataImportVM;
import com.hgicreate.rno.web.rest.vm.LteCellDataVM;
import com.hgicreate.rno.web.rest.vm.LteCellDescVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class LteCellDataService {


    private final LteCellDataRepository lteCellDataRepository;

    private final LteCellDescRepository lteCellDescRepository;

    private final DataJobRepository dataJobRepository;

    public LteCellDataService(LteCellDataRepository lteCellDataRepository, LteCellDescRepository lteCellDescRepository, DataJobRepository dataJobRepository) {
        this.lteCellDataRepository = lteCellDataRepository;
        this.lteCellDescRepository = lteCellDescRepository;
        this.dataJobRepository = dataJobRepository;
    }


    public List<LteCellDataDTO> queryLteCell(LteCellDataVM lteCellDataVM){
        LteCell lteCell = new LteCell();
        Area area = new Area();
        area.setId(Long.parseLong(lteCellDataVM.getCityId()));
        lteCell.setArea(area);

        if(!lteCellDataVM.getCellId().trim().equals("")){
            lteCell.setCellId(lteCellDataVM.getCellId().trim());
        }
        lteCell.setCellName(lteCellDataVM.getCellName().trim());
        ExampleMatcher matcher =  ExampleMatcher.matching()
                    .withMatcher("cellName", ExampleMatcher.GenericPropertyMatcher::contains)
                    .withIgnoreNullValues();
        if(!lteCellDataVM.getPci().trim().equals("")){
            lteCell.setPci(lteCellDataVM.getPci().trim());
        }
        Example<LteCell> example = Example.of(lteCell, matcher);
        List<LteCell> lteCells =lteCellDataRepository.findAll(example, new PageRequest(0,1000)).getContent();
        return lteCells.stream().map(LteCellDataMapper.INSTANCE::lteCellDataToLteCellDto).collect(Collectors.toList());
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


    public List<LteCellDescDTO> queryRecord(LteCellDescVM vm) throws ParseException{
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate = sdf.parse(vm.getBeginTestDate());
        Date endDate =sdf2.parse(vm.getEndTestDate() + " 23:59:59");
        List<LteCellDesc> list = lteCellDescRepository.findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
          Long.parseLong(vm.getCity()),
                beginDate,
                endDate
        );
        return list.stream().map(LteCellDescMapper.INSTANCE::lteCellDescToLteCellDescDTO)
                .collect(Collectors.toList());
    }
}
