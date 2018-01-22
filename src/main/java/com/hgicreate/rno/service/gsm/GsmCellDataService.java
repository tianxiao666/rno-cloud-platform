package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.domain.gsm.GsmCellDesc;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.repository.gsm.GsmCellDescRepository;
import com.hgicreate.rno.service.gsm.dto.GsmCellDataDTO;
import com.hgicreate.rno.service.gsm.dto.GsmCellDataFileDTO;
import com.hgicreate.rno.service.gsm.dto.GsmCellDescDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmCellDataFileMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmCellDataMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmCellDescMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCellDataImportVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCellDataVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmCellDescVM;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tao.xj
 */
@Service
public class GsmCellDataService {
    private final GsmCellDataRepository gsmCellDataRepository;

    private final GsmCellDescRepository gsmCellDescRepository;

    private final DataJobRepository dataJobRepository;

    public GsmCellDataService(GsmCellDataRepository gsmCellDataRepository, GsmCellDescRepository gsmCellDescRepository,
                              DataJobRepository dataJobRepository) {
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.gsmCellDescRepository = gsmCellDescRepository;

        this.dataJobRepository = dataJobRepository;
    }

    public List<GsmCellDataFileDTO> queryFileUploadRecord(GsmCellDataImportVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" );
        Date beginDate=sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date endDate =sdf2.parse(vm.getEndUploadDate() +" 23:59:59");
        List<DataJob> list;
        if("全部".equals(vm.getStatus())){
            list= dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate,"GSM-CELL-DATA");
        }else{

            list= dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate,"GSM-CELL-DATA");
        }
        return list.stream().map(GsmCellDataFileMapper.INSTANCE::gsmCellDataFileToGsmCellDataFileDto)
                .collect(Collectors.toList());
    }

    public List<GsmCellDescDTO> queryRecord(GsmCellDescVM vm) throws ParseException{
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date beginDate = sdf.parse(vm.getBeginCreatedDate()+" 00:00:00");
        Date endDate =sdf.parse(vm.getEndCreatedDate() + " 23:59:59");
        List<GsmCellDesc> list = gsmCellDescRepository.findTop1000ByArea_IdAndCreatedDateBetweenOrderByCreatedDateDesc(
                Long.parseLong(vm.getCity()), beginDate, endDate);
        return list.stream().map(GsmCellDescMapper.INSTANCE::gsmCellDescToGsmCellDescDTO)
                .collect(Collectors.toList());
    }

    public List<GsmCellDataDTO> queryGsmCell(GsmCellDataVM gsmCellDataVM){
        GsmCell gsmCell = new GsmCell();
        Area area = new Area();
        area.setId(Long.parseLong(gsmCellDataVM.getCityId()));
        gsmCell.setArea(area);

        if(!("".equals(gsmCellDataVM.getCellId().trim()))){
            gsmCell.setCellId(gsmCellDataVM.getCellId().trim());
        }
        gsmCell.setCellName(gsmCellDataVM.getCellName().trim());
        ExampleMatcher matcher =  ExampleMatcher.matching()
                .withMatcher("cellName", ExampleMatcher.GenericPropertyMatcher::contains)
                .withIgnoreNullValues();
        if(!"".equals(gsmCellDataVM.getBsc().trim())){
            gsmCell.setBsc(gsmCellDataVM.getBsc().trim());
        }
        Example<GsmCell> example = Example.of(gsmCell, matcher);
        List<GsmCell> gsmCells =gsmCellDataRepository.findAll(example, new PageRequest(0,1000)).getContent();
        return gsmCells.stream().map(GsmCellDataMapper.INSTANCE::gsmCellDataToGsmCellDto).collect(Collectors.toList());
    }
}
