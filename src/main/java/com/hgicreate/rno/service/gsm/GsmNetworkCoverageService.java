package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmCell;
import com.hgicreate.rno.domain.gsm.GsmNetworkCoverageJob;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.gsm.GsmCellDataRepository;
import com.hgicreate.rno.repository.gsm.GsmEriNcsDescRepository;
import com.hgicreate.rno.repository.gsm.GsmNetworkCoverageJobRepository;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForNetworkCoverageDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNetworkCoverageJobDTO;
import com.hgicreate.rno.service.gsm.mapper.GsmNcsForNetworkCoverageMapper;
import com.hgicreate.rno.service.gsm.mapper.GsmNetworkCoverageJobMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsForNetworkCoverageVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNetworkCoverageVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GsmNetworkCoverageService {
    private final GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository;

    private final GsmEriNcsDescRepository gsmEriNcsDescRepository;

    private final GsmCellDataRepository gsmCellDataRepository;
    private final AreaRepository areaRepository;

    public GsmNetworkCoverageService(GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository,
                                     GsmEriNcsDescRepository gsmEriNcsDescRepository,
                                     GsmCellDataRepository gsmCellDataRepository, AreaRepository areaRepository) {
        this.gsmNetworkCoverageJobRepository = gsmNetworkCoverageJobRepository;
        this.gsmEriNcsDescRepository = gsmEriNcsDescRepository;
        this.gsmCellDataRepository = gsmCellDataRepository;
        this.areaRepository = areaRepository;
    }

    public List<GsmNetworkCoverageJobDTO> jobQuery(GsmNetworkCoverageVM vm) throws ParseException {

        Area area = new Area();
        area.setId(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegDate());
        Date endDate = sdf.parse(vm.getEndDate());
        List<GsmNetworkCoverageJob> list = gsmNetworkCoverageJobRepository.findTop1000ByAreaAndCreatedDateBetween(
                area,beginDate,endDate);
        return list.stream()
                .map(GsmNetworkCoverageJobMapper.INSTANCE::gsmNetworkCoverageToGsmNetworkCoverageDTO)
                .collect(Collectors.toList());
    }

    public List<GsmNcsForNetworkCoverageDTO> ncsDataQuery(GsmNcsForNetworkCoverageVM vm) throws ParseException {
        Area area = new Area();
        area.setId(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegMeaDate());
        Date endDate = sdf.parse(vm.getEndMeaDate());
        return gsmEriNcsDescRepository.findTop1000ByAreaAndMeaTimeBetween(area,beginDate,endDate)
                .stream()
                .map(GsmNcsForNetworkCoverageMapper.INSTANCE::ncsForNetCoverToNcsForNetCoverDTO)
                .collect(Collectors.toList());
    }

    public File saveGsmNetworkCoverageResult(String jobId,Long cityId){

        List<GsmCell> cellList = gsmCellDataRepository.findByArea_Id(cityId);
        Area area = areaRepository.findById(cityId);
        String directory ="d:/tmp/rno-cloud-platform/downloads/";
        File fileDirectory = new File(directory);
        if(!fileDirectory.exists()){
            fileDirectory.mkdirs();
        }
        File csvFile = new File(directory+area.getName()+"_"+jobId+"_2G方向角.csv");
        if(!csvFile.exists()){
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bw=null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(csvFile, true), "gbk"));
            //标题
            bw.write("城市,小区名,CELLID,现网方向角,计算方向角,方向角差值");
            bw.newLine();
            String cityName = area.getName();
            for(GsmCell gsmCell:cellList){
                String cellName = gsmCell.getCellName();
                String cellId = gsmCell.getCellId();
                int calAzimuth = (int)(Math.random()*359);
                String line;
                if(gsmCell.getAzimuth()==null) {
                    int diffAzimuth = calAzimuth>180?Math.abs(360-calAzimuth):calAzimuth;
                    line = "\""+cityName
                            +"\",\""+cellName
                            +"\",\""+cellId
                            +"\",\""+""
                            +"\",\""+calAzimuth
                            +"\",\""+diffAzimuth+"\"";
                } else {
                    int currentAzimuth = gsmCell.getAzimuth();
                    int diffAzimuth = Math.abs(calAzimuth-currentAzimuth);
                    int curAzimuthInt = diffAzimuth>180?360-diffAzimuth:diffAzimuth;
                    line = "\""+cityName
                            +"\",\""+cellName
                            +"\",\""+cellId
                            +"\",\""+currentAzimuth
                            +"\",\""+calAzimuth
                            +"\",\""+curAzimuthInt+"\"";
                }
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (bw != null) {
                    bw.flush();
                }
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return csvFile;
    }
}