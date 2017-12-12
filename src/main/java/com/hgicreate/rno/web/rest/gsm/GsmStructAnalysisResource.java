package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmStructAnalysisJob;
import com.hgicreate.rno.repository.gsm.GsmStructAnalysisJobRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.GsmStructAnalysisService;
import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import com.hgicreate.rno.service.gsm.dto.GsmThresholdDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructAnalysisQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructTaskInfoVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructTaskParamsVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gsm-struct-analysis")
public class GsmStructAnalysisResource {
    private final GsmStructAnalysisService gsmStructAnalysisService;
    private final GsmStructAnalysisJobRepository gsmStructAnalysisJobRepository;

    public GsmStructAnalysisResource(GsmStructAnalysisService gsmStructAnalysisService,
                                     GsmStructAnalysisJobRepository gsmStructAnalysisJobRepository) {
        this.gsmStructAnalysisService = gsmStructAnalysisService;
        this.gsmStructAnalysisJobRepository = gsmStructAnalysisJobRepository;
    }

    @PostMapping("/task-query")
    public List<GsmStructAnalysisJobDTO> taskQuery(GsmStructAnalysisQueryVM vm) throws ParseException {
        return gsmStructAnalysisService.taskQuery(vm);
    }

    @GetMapping("/get-task-params")
    public List<GsmThresholdDTO> getTaskParams(){
        return gsmStructAnalysisService.getTaskParams();
    }

    @PostMapping("/query-file-number")
    public List<Map<String,Object>> queryFileNumber(GsmStructTaskInfoVM vm) throws ParseException {
        return gsmStructAnalysisService.queryFileNumber(vm);
    }

    @PostMapping("/submit-task")
    public void submitTask(GsmStructTaskInfoVM vm) throws ParseException {
        log.debug("任务信息：{}",vm);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date begMeaDate = sdf.parse(vm.getBegMeaDate());
        Date endMeaDate = sdf.parse(vm.getEndMeaDate());

        Date createdDate = new Date();

        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.setTime(createdDate);
        startTimeCal.add(Calendar.SECOND,2);
        Date startTime = startTimeCal.getTime();
        startTimeCal.add(Calendar.SECOND,30);
        Date completeTime = startTimeCal.getTime();

        List<Map<String,Object>> list = gsmStructAnalysisService.queryFileNumber(vm);
        int fileNum = 0;
        for(Map<String,Object> map:list){
            if(!("--".equals(map.get("mrrNum")))){
                fileNum = fileNum+Integer.parseInt(map.get("mrrNum").toString());
            }
            if(!("--".equals(map.get("ncsNum")))){
                fileNum = fileNum+Integer.parseInt(map.get("ncsNum").toString());
            }
            if(!("--".equals(map.get("bscNum")))){
                fileNum = fileNum+Integer.parseInt(map.get("bscNum").toString());
            }
        }

        Area area = new Area();
        area.setId(vm.getCityId());

        GsmStructAnalysisJob gsmStructAnalysisJob = new GsmStructAnalysisJob();
        gsmStructAnalysisJob.setName(vm.getJobName());
        gsmStructAnalysisJob.setArea(area);
        gsmStructAnalysisJob.setDescription(vm.getTaskDescription());
        gsmStructAnalysisJob.setPriority(1);
        gsmStructAnalysisJob.setBegMeaTime(begMeaDate);
        gsmStructAnalysisJob.setEndMeaTime(endMeaDate);
        gsmStructAnalysisJob.setCreatedDate(createdDate);
        gsmStructAnalysisJob.setStartTime(startTime);
        gsmStructAnalysisJob.setCompleteTime(completeTime);
        gsmStructAnalysisJob.setStatus("正常完成");
        gsmStructAnalysisJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
        gsmStructAnalysisJob.setFileNumber(fileNum);
        gsmStructAnalysisJobRepository.save(gsmStructAnalysisJob);
    }
}
