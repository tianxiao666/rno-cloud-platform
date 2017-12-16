package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmNetworkCoverageJob;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.gsm.GsmNetworkCoverageJobRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.GsmNetworkCoverageService;
import com.hgicreate.rno.service.gsm.dto.GsmNcsForNetworkCoverageDTO;
import com.hgicreate.rno.service.gsm.dto.GsmNetworkCoverageJobDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNcsForNetworkCoverageVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmNetworkCoverageVM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/gsm-network-coverage")
public class GsmNetworkCoverageJobResource {
    private final GsmNetworkCoverageService gsmNetworkCoverageService;

    private final GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository;
    private final AreaRepository areaRepository;

    public GsmNetworkCoverageJobResource(GsmNetworkCoverageService gsmNetworkCoverageService,
                                         GsmNetworkCoverageJobRepository gsmNetworkCoverageJobRepository,
                                         AreaRepository areaRepository) {
        this.gsmNetworkCoverageService = gsmNetworkCoverageService;
        this.gsmNetworkCoverageJobRepository = gsmNetworkCoverageJobRepository;
        this.areaRepository = areaRepository;
    }

    @PostMapping("/job-query")
    public List<GsmNetworkCoverageJobDTO> jobQuery(GsmNetworkCoverageVM vm) throws ParseException {
        log.debug("视图模型：{}",vm);
        return gsmNetworkCoverageService.jobQuery(vm);
    }

    @PostMapping("/ncs-data-query")
    public List<GsmNcsForNetworkCoverageDTO> ncsDataQuery(GsmNcsForNetworkCoverageVM vm) throws ParseException {
        log.debug("视图模型：{}",vm);
        return gsmNetworkCoverageService.ncsDataQuery(vm);
    }

    @PostMapping("/add-job")
    public boolean addNetCoverJob(GsmNcsForNetworkCoverageVM vm) throws ParseException {
        log.debug("视图模型：{}",vm);
        Area area = areaRepository.findById(vm.getCityId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beginDate = sdf.parse(vm.getBegMeaDate());
        Date endDate = sdf.parse(vm.getEndMeaDate());
        GsmNetworkCoverageJob job = new GsmNetworkCoverageJob();
        job.setArea(area);
        Date createdDate = new Date();
        job.setBegMeaTime(beginDate);
        job.setEndMeaTime(endDate);
        Calendar now = Calendar.getInstance();
        job.setName(area.getName()+now.get(Calendar.MONDAY)+"_"+now.get(Calendar.DATE)+"网络覆盖分析任务");
        job.setPriority(1);
        job.setStartTime(createdDate);
        now.add(Calendar.SECOND,30);
        job.setCompleteTime(now.getTime());
        job.setStatus("正常完成");
        job.setCreatedUser(SecurityUtils.getCurrentUserLogin());
        job.setCreatedDate(createdDate);
        List<GsmNcsForNetworkCoverageDTO> dtoList = gsmNetworkCoverageService.ncsDataQuery(vm);
        if(dtoList==null||dtoList.size()<=0){
            return false;
        }
        int fileNumber = 0;
        for(GsmNcsForNetworkCoverageDTO ncs:dtoList){
            fileNumber += ncs.getRecordCount();
        }
        job.setFileNumber(fileNumber);
        gsmNetworkCoverageJobRepository.save(job);
        return true;
    }

    @GetMapping("/download-result")
    @ResponseBody
    public ResponseEntity<byte[]> downloadResultFile(String id) {
        log.debug("覆盖分析任务id：{}",id);
        GsmNetworkCoverageJob gsmNetworkCoverageJob = gsmNetworkCoverageJobRepository.findOne(Long.parseLong(id));
        Area area = gsmNetworkCoverageJob.getArea();
        File file = gsmNetworkCoverageService.saveGsmNetworkCoverageResult(id,area.getId());
        try {
            HttpHeaders headers = new HttpHeaders();
            String fileName = new String(file.getName().getBytes("UTF-8"),
                    "iso-8859-1");
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }finally {
            if (file.delete()) {
                log.debug("临时文件删除成功。");
            } else {
                log.debug("临时文件删除失败。");
            }
        }
    }
}
