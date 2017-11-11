package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.Cell;
import com.hgicreate.rno.repository.LteCellDataRepository;
import com.hgicreate.rno.service.dto.DataCollectDTO;
import com.hgicreate.rno.service.dto.LteCellDataDTO;
import com.hgicreate.rno.service.dto.LteCellDataRecordDTO;
import com.hgicreate.rno.service.mapper.LteCellDataMapper;
import com.hgicreate.rno.web.rest.vm.LteCellDataVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class LteCellDataService {


    private final LteCellDataRepository lteCellDataRepository;

    public LteCellDataService(LteCellDataRepository lteCellDataRepository) {
        this.lteCellDataRepository = lteCellDataRepository;
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

    public List<DataCollectDTO> queryFileUploadRecord(){
        List<DataCollectDTO> dtoList = new ArrayList<>();
        dtoList.add(new DataCollectDTO(1, "广州", "2015-09-9 11:35:49",
                "广州天河区LTE小区数据.csv", "1336235", "2015-09-9 11:36:09",
                "2015-10-9 11:36:41", "liu.yp@iscreate.com", "部分失败"));
        dtoList.add(new DataCollectDTO(2, "广州", "2015-9-23 18:18:41",
                "广州荔湾区LTE小区数据.csv", "19491001", "2015-9-23 18:19:03",
                "2015-9-23 18:19:35", "liu.yp@iscreate.com", "全部成功"));
        dtoList.add(new DataCollectDTO(3, "广州", "2015-9-13 11:59:09",
                "广州海珠区LTE小区数据.csv", "1348128", "2015-9-13 12:05:05",
                "2015-9-13 12:19:20", "liu.yp@iscreate.com", "全部成功"));
        return dtoList;
    }

    public List<LteCellDataRecordDTO> queryRecord(){
        List<LteCellDataRecordDTO> list = new ArrayList<>();
        list.add(new LteCellDataRecordDTO("广州","2015-06-18 09:33:05","2015-06-18 09:36:13","4218","63243","2015-06-18 09:32:55"));
        list.add(new LteCellDataRecordDTO("广州","2015-06-20 13:45:11","2015-06-20 13:49:01","4217","52134","2015-06-20 13:44:11"));
        list.add(new LteCellDataRecordDTO("广州","2015-01-03 15:21:01","2015-01-03 15:22:12","4219","5543","2015-01-03 15:20:08"));
        return list;
    }
}
