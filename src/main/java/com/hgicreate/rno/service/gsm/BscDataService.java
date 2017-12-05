package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.BscData;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.BscDataRepository;
import com.hgicreate.rno.service.gsm.dto.BscDataDTO;
import com.hgicreate.rno.service.gsm.dto.BscReportDTO;
import com.hgicreate.rno.service.gsm.mapper.BscDataFileMapper;
import com.hgicreate.rno.service.gsm.mapper.BscDataMessageMapper;
import com.hgicreate.rno.web.rest.gsm.vm.BscDataQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.BscImportQueryVM;
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

@Service
public class BscDataService {

    private final DataJobRepository dataJobRepository;
    private final BscDataRepository bscDataRepository;

    public BscDataService(DataJobRepository dataJobRepository, BscDataRepository bscDataRepository) {
        this.dataJobRepository = dataJobRepository;
        this.bscDataRepository = bscDataRepository;
    }

    public List<BscReportDTO> queryTrafficData(BscImportQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = sdf2.parse(vm.getEndUploadDate() + " 23:59:59");
        List<DataJob> list;
        if (vm.getStatus().equals("全部")) {
            list = dataJobRepository
                    .findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate, "GSM-BSC-DATA");
        } else {
            list = dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate, "GSM-BSC-DATA");
        }
        return list.stream().map(BscDataFileMapper.INSTANCE::bscDataFileToBscDataDTO)
                .collect(Collectors.toList());
    }

    public List<BscDataDTO> queryRecord(BscDataQueryVM vm) throws ParseException {
        BscData bscData = new BscData();
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityIds()));
        bscData.setArea(area);
        if(!("".equals(vm.getBsc().trim()))){
          bscData.setBsc(vm.getBsc().trim());
        }
        if(!("".equals(vm.getVendor().trim()))){
            bscData.setVendor(vm.getVendor().trim());
        }
        ExampleMatcher matcher =  ExampleMatcher.matching()
                .withMatcher("bsc", ExampleMatcher.GenericPropertyMatcher::contains)
                .withMatcher("vendor", ExampleMatcher.GenericPropertyMatcher::contains)
                .withIgnoreNullValues();
        Example<BscData> example = Example.of(bscData, matcher);
        return bscDataRepository
                .findAll(example, new PageRequest(0,1000)).getContent()
                .stream().map(BscDataMessageMapper.INSTANCE::bscDataToBscDataDto)
                .collect(Collectors.toList());
    }

}
