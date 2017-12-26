package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.gsm.GsmStructAnalysisJob;
import com.hgicreate.rno.domain.gsm.GsmStructJobParam;
import com.hgicreate.rno.domain.gsm.GsmStructJobReport;
import com.hgicreate.rno.repository.gsm.GsmStructAnalysisJobRepository;
import com.hgicreate.rno.repository.gsm.GsmStructJobParamRepository;
import com.hgicreate.rno.repository.gsm.GsmStructJobReportRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.gsm.GsmStructAnalysisService;
import com.hgicreate.rno.service.gsm.dto.GsmStructAnalysisJobDTO;
import com.hgicreate.rno.service.gsm.dto.StructJobReportDTO;
import com.hgicreate.rno.service.gsm.mapper.StructJobReportMapper;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructAnalysisQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmStructTaskInfoVM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/gsm-struct-analysis")
public class GsmStructAnalysisResource {
    private final GsmStructAnalysisService gsmStructAnalysisService;
    private final GsmStructAnalysisJobRepository gsmStructAnalysisJobRepository;

    private final GsmStructJobParamRepository gsmStructJobParamRepository;
    private final GsmStructJobReportRepository gsmStructJobReportRepository;

    public GsmStructAnalysisResource(GsmStructAnalysisService gsmStructAnalysisService,
                                     GsmStructAnalysisJobRepository gsmStructAnalysisJobRepository,
                                     GsmStructJobParamRepository gsmStructJobParamRepository,
                                     GsmStructJobReportRepository gsmStructJobReportRepository) {
        this.gsmStructAnalysisService = gsmStructAnalysisService;
        this.gsmStructAnalysisJobRepository = gsmStructAnalysisJobRepository;
        this.gsmStructJobParamRepository = gsmStructJobParamRepository;
        this.gsmStructJobReportRepository = gsmStructJobReportRepository;
    }

    @PostMapping("/task-query")
    public List<GsmStructAnalysisJobDTO> taskQuery(GsmStructAnalysisQueryVM vm) throws ParseException {
        return gsmStructAnalysisService.taskQuery(vm);
    }

    @PostMapping("/query-file-number")
    public List<Map<String,Object>> queryFileNumber(GsmStructTaskInfoVM vm) throws ParseException {
        return gsmStructAnalysisService.queryFileNumber(vm);
    }

    @PostMapping("/query-report")
    public List<StructJobReportDTO> queryReport(String id){
        log.debug("查询任务报告的任务id：{}",id);
        return gsmStructJobReportRepository.findByGsmStructAnalysisJob_IdOrderByStatusDesc(Long.parseLong(id))
                .stream().map(StructJobReportMapper.INSTANCE::structJobReportToStructJobReportDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/download-result")
    @ResponseBody
    public ResponseEntity<byte[]> downloadResultFile(String id){
        GsmStructAnalysisJob gsmStructAnalysisJob = gsmStructAnalysisJobRepository.findOne(Long.parseLong(id));
        Area area = gsmStructAnalysisJob.getArea();
        String resultFilePath = gsmStructAnalysisService.saveGsmStructAnaResult(area.getId());
        File file = new File(resultFilePath);
        try {
            HttpHeaders headers = new HttpHeaders();
            String fileName = new String(file.getName().getBytes("UTF-8"),
                    "iso-8859-1");
            log.debug("结构优化结果文件名称：{}",fileName);
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

    @PostMapping("/submit-task")
    public void submitTask(GsmStructTaskInfoVM vm) throws ParseException {
        log.debug("任务信息：{}",vm);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

        // 保存任务信息
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
        if(fileNum == 0){
            gsmStructAnalysisJob.setStatus("异常终止");
            gsmStructAnalysisJob.setCompleteTime(addSecond(startTime,1));
        }else {
            gsmStructAnalysisJob.setStatus("正常完成");
        }
        gsmStructAnalysisJob.setCreatedUser(SecurityUtils.getCurrentUserLogin());
        gsmStructAnalysisJob.setFileNumber(fileNum);
        gsmStructAnalysisJobRepository.save(gsmStructAnalysisJob);

        // 保存任务参数
        List<GsmStructJobParam> gsmStructJobParamList = new ArrayList<>();
        List<Map<String,Object>> Paramlist = gsmStructAnalysisService.getParamsInfo(vm);
        for(Map map:Paramlist){
            GsmStructJobParam gsmStructJobParam = new GsmStructJobParam();
            gsmStructJobParam.setGsmStructAnalysisJob(gsmStructAnalysisJob);
            gsmStructJobParam.setParamType(map.get("type").toString());
            gsmStructJobParam.setParamCode(map.get("name").toString());
            gsmStructJobParam.setParamVal(map.get("value").toString());
            gsmStructJobParamList.add(gsmStructJobParam);
        }
        gsmStructJobParamRepository.save(gsmStructJobParamList);

        // 保存任务报告
        List<GsmStructJobReport> gsmStructJobReportList = new ArrayList<>();
        GsmStructJobReport gsmStructJobReport = new GsmStructJobReport();
        gsmStructJobReport.setGsmStructAnalysisJob(gsmStructAnalysisJob);
        if(fileNum!=0){
            if(vm.getUseEriData()!=null){
                gsmStructJobReport.setStage("完成爱立信NCS数据准备");
                gsmStructJobReport.setStartTime(startTime);
                gsmStructJobReport.setCompleteTime(addSecond(gsmStructJobReport.getStartTime(),2));
                gsmStructJobReport.setStatus("成功");
                gsmStructJobReportList.add(gsmStructJobReport);

                GsmStructJobReport gsmStructJobReport2 = new GsmStructJobReport();
                gsmStructJobReport2.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                gsmStructJobReport2.setStage("完成爱立信MRR数据准备");
                gsmStructJobReport2.setStartTime(gsmStructJobReport.getCompleteTime());
                gsmStructJobReport2.setCompleteTime(addSecond(gsmStructJobReport2.getStartTime(),4));
                gsmStructJobReport2.setStatus("成功");
                gsmStructJobReportList.add(gsmStructJobReport2);

                if(vm.getUseHwData()!=null){
                    GsmStructJobReport gsmStructJobReport3 = new GsmStructJobReport();
                    gsmStructJobReport3.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                    gsmStructJobReport3.setStage("完成华为NCS数据准备");
                    gsmStructJobReport3.setStartTime(gsmStructJobReport2.getCompleteTime());
                    gsmStructJobReport3.setCompleteTime(addSecond(gsmStructJobReport3.getStartTime(),3));
                    gsmStructJobReport3.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport3);

                    GsmStructJobReport gsmStructJobReport4 = new GsmStructJobReport();
                    gsmStructJobReport4.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                    gsmStructJobReport4.setStage("完成华为MRR数据准备");
                    gsmStructJobReport4.setStartTime(gsmStructJobReport3.getCompleteTime());
                    gsmStructJobReport4.setCompleteTime(addSecond(gsmStructJobReport4.getStartTime(),5));
                    gsmStructJobReport4.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport4);
                }

                if(vm.getCalConCluster()!=null){
                    GsmStructJobReport gsmStructJobReport5 = new GsmStructJobReport();
                    gsmStructJobReport5.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                    gsmStructJobReport5.setStage("计算最大联通簇");
                    gsmStructJobReport5.setStartTime(gsmStructJobReportList.get(gsmStructJobReportList.size()-1)
                            .getCompleteTime());
                    gsmStructJobReport5.setCompleteTime(addSecond(gsmStructJobReport5.getStartTime(),3));
                    gsmStructJobReport5.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport5);
                }
                if(vm.getCalClusterConstrain()!=null){
                    GsmStructJobReport gsmStructJobReport6 = new GsmStructJobReport();
                    gsmStructJobReport6.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                    gsmStructJobReport6.setStage("计算簇约束因子");
                    gsmStructJobReport6.setStartTime(gsmStructJobReportList.get(gsmStructJobReportList.size()-1)
                            .getCompleteTime());
                    gsmStructJobReport6.setCompleteTime(addSecond(gsmStructJobReport6.getStartTime(),4));
                    gsmStructJobReport6.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport6);
                }
                if(vm.getCalClusterWeight()!=null){
                    GsmStructJobReport gsmStructJobReport7 = new GsmStructJobReport();
                    gsmStructJobReport7.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                    gsmStructJobReport7.setStage("计算簇权重");
                    gsmStructJobReport7.setStartTime(gsmStructJobReportList.get(gsmStructJobReportList.size()-1)
                            .getCompleteTime());
                    gsmStructJobReport7.setCompleteTime(addSecond(gsmStructJobReport7.getStartTime(),3));
                    gsmStructJobReport7.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport7);
                }
                if(vm.getCalCellRes()!=null){
                    GsmStructJobReport gsmStructJobReport8 = new GsmStructJobReport();
                    gsmStructJobReport8.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                    gsmStructJobReport8.setStage("计算结构指数");
                    gsmStructJobReport8.setStartTime(gsmStructJobReportList.get(gsmStructJobReportList.size()-1)
                            .getCompleteTime());
                    gsmStructJobReport8.setCompleteTime(addSecond(gsmStructJobReport8.getStartTime(),2));
                    gsmStructJobReport8.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport8);
                }
                if(vm.getCalIdealDis()!=null){
                    GsmStructJobReport gsmStructJobReport9 = new GsmStructJobReport();
                    gsmStructJobReport9.setGsmStructAnalysisJob(gsmStructAnalysisJob);
                    gsmStructJobReport9.setStage("计算理想距离");
                    gsmStructJobReport9.setStartTime(gsmStructJobReportList.get(gsmStructJobReportList.size()-1)
                            .getCompleteTime());
                    gsmStructJobReport9.setCompleteTime(addSecond(gsmStructJobReport9.getStartTime(),3));
                    gsmStructJobReport9.setStatus("成功");
                    gsmStructJobReportList.add(gsmStructJobReport9);
                }
            }
            GsmStructJobReport gsmStructJobReport10 = new GsmStructJobReport();
            gsmStructJobReport10.setGsmStructAnalysisJob(gsmStructAnalysisJob);
            gsmStructJobReport10.setStage("保存分析结果");
            gsmStructJobReport10.setStartTime(gsmStructJobReportList.get(gsmStructJobReportList.size()-1)
                    .getCompleteTime());
            gsmStructJobReport10.setCompleteTime(completeTime);
            gsmStructJobReport10.setStatus("成功");
            gsmStructJobReportList.add(gsmStructJobReport10);

            GsmStructJobReport gsmStructJobReport11 = new GsmStructJobReport();
            gsmStructJobReport11.setGsmStructAnalysisJob(gsmStructAnalysisJob);
            gsmStructJobReport11.setStage("任务总结");
            gsmStructJobReport11.setStartTime(startTime);
            gsmStructJobReport11.setCompleteTime(completeTime);
            gsmStructJobReport11.setStatus("全部成功");
            gsmStructJobReport11.setMessage("结构分析完成！");
            gsmStructJobReportList.add(gsmStructJobReport11);
        }else{
            gsmStructJobReport.setStage("任务总结");
            gsmStructJobReport.setStartTime(gsmStructAnalysisJob.getStartTime());
            gsmStructJobReport.setCompleteTime(gsmStructAnalysisJob.getCompleteTime());
            gsmStructJobReport.setStatus("失败");
            gsmStructJobReport.setMessage("结构优化分析异常！没有数据！");
            gsmStructJobReportList.add(gsmStructJobReport);
        }
        gsmStructJobReportRepository.save(gsmStructJobReportList);
    }

    private Date addSecond(Date date,int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }
}
