package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.GsmBscData;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.GsmBscDataRepository;
import com.hgicreate.rno.service.gsm.dto.GsmBscDataDTO;
import com.hgicreate.rno.service.gsm.dto.GsmBscReportDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmBscDataFileMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmBscDataMessageMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmBscDataQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmBscImportQueryVM;
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
 * @author zeng.dh1
 */

@Service
public class GsmBscDataService {

    private final DataJobRepository dataJobRepository;
    private final GsmBscDataRepository gsmBscDataRepository;

    public GsmBscDataService(DataJobRepository dataJobRepository, GsmBscDataRepository gsmBscDataRepository) {
        this.dataJobRepository = dataJobRepository;
        this.gsmBscDataRepository = gsmBscDataRepository;
    }

    public List<GsmBscReportDTO> queryTrafficData(GsmBscImportQueryVM vm) throws ParseException {
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCity()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = sdf.parse(vm.getBegUploadDate());
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date endDate = sdf2.parse(vm.getEndUploadDate() + " 23:59:59");
        List<DataJob> list;
        if ("全部".equals(vm.getStatus())) {
            list = dataJobRepository
                    .findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, beginDate, endDate, "GSM-BSC-DATA");
        } else {
            list = dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(
                    area, vm.getStatus(), beginDate, endDate, "GSM-BSC-DATA");
        }
        return list.stream().map(GsmBscDataFileMapper.INSTANCE::bscDataFileToBscDataDTO)
                .collect(Collectors.toList());
    }

    public List<GsmBscDataDTO> queryRecord(GsmBscDataQueryVM vm){
        GsmBscData gsmBscData = new GsmBscData();
        Area area = new Area();
        area.setId(Long.parseLong(vm.getCityIds()));
        gsmBscData.setArea(area);
        if(!("".equals(vm.getBsc().trim()))){
          gsmBscData.setBsc(vm.getBsc().trim());
        }
        if(!("".equals(vm.getVendor().trim()))){
            gsmBscData.setVendor(vm.getVendor().trim());
        }
        ExampleMatcher matcher =  ExampleMatcher.matching()
                .withMatcher("bsc", ExampleMatcher.GenericPropertyMatcher::contains)
                .withMatcher("vendor", ExampleMatcher.GenericPropertyMatcher::contains)
                .withIgnoreNullValues();
        Example<GsmBscData> example = Example.of(gsmBscData, matcher);
        return gsmBscDataRepository
                .findAll(example, new PageRequest(0,1000)).getContent()
                .stream().map(GsmBscDataMessageMapper.INSTANCE::bscDataToBscDataDto)
                .collect(Collectors.toList());
    }

}
